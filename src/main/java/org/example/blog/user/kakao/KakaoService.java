package org.example.blog.user.kakao;

import lombok.RequiredArgsConstructor;
import org.example.blog._core.errors.exception.Exception401;
import org.example.blog.user.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoService {
    private final UserService userService;
    private final UserRepository userRepository;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${social.key}")
    private String socialKey;

    @Value("${oauth.kakao.client-secret}")
    private String clientSecret;

    private final PasswordEncoder passwordEncoder;
    @Transactional
    public User kakaoLogin(String code) {
        // 1. 인가코드로 액세스 토큰 발급
        UserResponse.OAuthToken oAuthToken = generateAccessTokenForKakao(code);

        // 2. 액세스 토큰으로 프로필 정보 조회
        UserResponse.KaKaoProfile kaKaoProfile = getKakaoProfile(oAuthToken.getAccessToken());

        // 3. 프로필 정보로 사용자 생성 또는 조회
        User user = generateKakaoUser(kaKaoProfile);

        // 4. 로그인 처리(엔티티 반환)
        return user;
    }

    private UserResponse.OAuthToken generateAccessTokenForKakao(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("grant_type", "authorization_code");
        tokenParams.add("client_id", clientId);
        tokenParams.add("redirect_uri", "http://localhost:8080/user/kakao");
        tokenParams.add("code", code);
        tokenParams.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams, tokenHeaders);
        ResponseEntity<UserResponse.OAuthToken> tokenResponse = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                tokenRequest,
                UserResponse.OAuthToken.class
        );

        UserResponse.OAuthToken oAuthToken = tokenResponse.getBody();
        return oAuthToken;
    }

    private UserResponse.KaKaoProfile getKakaoProfile(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders profileHeaders = new HttpHeaders();

        profileHeaders.add("Authorization", "Bearer " + accessToken);
        profileHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<Void> profileRequest = new HttpEntity<>(profileHeaders);

        ResponseEntity<UserResponse.KaKaoProfile> profileResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                profileRequest,
                UserResponse.KaKaoProfile.class
        );

        UserResponse.KaKaoProfile kaKaoProfile = profileResponse.getBody();
        if(kaKaoProfile == null) {
            throw new Exception401("카카오 프로필 조회에 실패했습니다.");
        }
        return kaKaoProfile;
    }

    @Transactional
    public User generateKakaoUser(UserResponse.KaKaoProfile kaKaoProfile) {
        String usernameFromKakao =  kaKaoProfile.getProperties().getNickname() + "_" + "kakao";

        User user = findUsername(usernameFromKakao);
        if(user == null) {
            User newUser = User.builder()
                    .username(usernameFromKakao)
                    .password(passwordEncoder.encode(socialKey))
                    .email(null)
                    .provider(OAuthProvider.KAKAO)
                    .build();

            String profileImage = kaKaoProfile.getProperties().getProfileImage();
            if(profileImage != null && !profileImage.isEmpty()) {
                newUser.setProfileImage(profileImage);
            }
            userService.socialJoin(newUser);
            user = newUser;
        }
        return user;
    }

    private User findUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }
}
