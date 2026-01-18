package org.example.blog.user.naver;

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
public class NaverService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${oauth.naver.client-id}")
    private String clientId;

    @Value("${oauth.naver.client-secret}")
    private String clientSecret;

    @Value("${oauth.naver.redirect-uri}")
    private String redirectUri;

    @Value("${social.key}")
    private String socialKey;

    @Transactional
    public User naverLogin(String code, String state) {
        UserResponse.OAuthToken token = generateAccessTokenForNaver(code, state);
        UserResponse.NaverProfile naverProfile = getNaverProfile(token.getAccessToken());
        User user = generateNaverUser(naverProfile);
        return user;
    }

    @Transactional
    public UserResponse.OAuthToken generateAccessTokenForNaver(String code, String state) {
        // 헤더
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 바디
        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("grant_type", "authorization_code");
        tokenParams.add("client_id", clientId);
        tokenParams.add("client_secret", clientSecret);
        tokenParams.add("code", code);
        tokenParams.add("state", state);
        tokenParams.add("redirect_uri", redirectUri); // 문서에는 필수값 아님

        // HttpEntity 객체
        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams, tokenHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<UserResponse.OAuthToken> tokenResponse = restTemplate.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                tokenRequest,
                UserResponse.OAuthToken.class
        );

        UserResponse.OAuthToken oAuthToken = tokenResponse.getBody();
        if(oAuthToken == null) {
            throw new Exception401("네이버 인증에 실패하였습니다.");
        }
        return oAuthToken;
    }

    private UserResponse.NaverProfile getNaverProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> profileRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<UserResponse.NaverProfile> profileResponse = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                profileRequest,
                UserResponse.NaverProfile.class
        );

        UserResponse.NaverProfile profile = profileResponse.getBody();
        if(profile == null) {
            throw new Exception401("네이버 프로필 조회에 실패하였습니다.");
        }
        return profile;
    }

    @Transactional
    public User generateNaverUser(UserResponse.NaverProfile naverProfile) {
        String usernameFromNaver =  naverProfile.getResponse().getName() + "_" + "naver";

        User userEntity = userRepository.findByUsername(usernameFromNaver)
                .orElse(null);
        if(userEntity == null) {
            User newUser = User.builder()
                    .username(usernameFromNaver)
                    .password(passwordEncoder.encode(socialKey))
                    .email(naverProfile.getResponse().getEmail())
                    .provider(OAuthProvider.NAVER)
                    .build();

            String profileImage = naverProfile.getResponse().getProfileImage();
            if(profileImage != null && !profileImage.isEmpty()) {
                newUser.setProfileImage(profileImage);
            }
            userService.socialJoin(newUser);
            userEntity = newUser;
        }
        return userEntity;
    }
}
