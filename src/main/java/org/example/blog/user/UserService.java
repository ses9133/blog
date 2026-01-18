package org.example.blog.user;

import lombok.RequiredArgsConstructor;
import org.example.blog._core.errors.exception.Exception400;
import org.example.blog._core.errors.exception.Exception403;
import org.example.blog._core.errors.exception.Exception404;
import org.example.blog._core.errors.exception.Exception500;
import org.example.blog._core.utils.FileUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void joinProc(UserRequest.JoinDTO joinDTO) {
        if (userRepository.findByUsername(joinDTO.getUsername()).isPresent()) {
            throw new Exception400("이미 존재하는 사용자 이름입니다.");
        }

        if(userRepository.findByEmail(joinDTO.getEmail()).isPresent()) {
            throw new Exception400("이미 등록된 이메일 입니다.");
        }

        String profileImageFileName = null;

        if (joinDTO.getProfileImage() != null && !joinDTO.getProfileImage().isEmpty()) {
            try {
                if (!FileUtil.isImageFile(joinDTO.getProfileImage())) {
                    throw new Exception400("이미지 파일만 업로드 가능합니다.");
                }
                profileImageFileName = FileUtil.saveFile(joinDTO.getProfileImage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String hashPwd = passwordEncoder.encode(joinDTO.getPassword());

        User user = joinDTO.toEntity(profileImageFileName);
        user.setPassword(hashPwd);

        userRepository.save(user);
    }

    @Transactional
    public User login(UserRequest.LoginDTO loginDTO) {
        User user = userRepository.findByUsernameWithRoles(loginDTO.getUsername())
                .orElse(null);

        if (user == null) {
            throw new Exception400("사용자가 존재하지 않습니다.");
        }

        // 비밀번호 검증 (BCrypt matches 메서드를 사용해서 비교하면 된다.)
        // 일치하면 true, 불일치하면 false 반환
//        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
//            System.out.println("사용자명 또는 비밀번호가 올바르지 않습니다.");
//            throw new Exception400("사용자명 또는 비밀번호가 올바르지 않습니다.");
//        }
        // TODO - 개발중에만
        if(!loginDTO.getPassword().equals(user.getPassword()))  {
            System.out.println("사용자명 또는 비밀번호가 올바르지 않습니다.");
            throw new Exception400("사용자명 또는 비밀번호가 올바르지 않습니다.");
        }
        return user;
    }

    public User getMyPage(Long sessionUserId) {
        User user = userRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));
        if (!user.isOwner(sessionUserId)) {
            throw new Exception403("조회 권한이 없습니다.");
        }
        return user;
    }

    public User updateForm(Long userId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));
        if (!userEntity.isOwner(userId)) {
            throw new Exception403("수정 권한이 없습니다.");
        }
        return userEntity;
    }

    @Transactional
    public UserResponse.DetailDTO updateProc(UserRequest.UpdateDTO updateDTO, Long userId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));
        if (!userEntity.isOwner(userId)) {
            throw new Exception403("수정 권한이 없습니다.");
        }

        String oldProfileImage = userEntity.getProfileImage();
        if(updateDTO.getProfileImage() != null && !updateDTO.getProfileImage().isEmpty()) {
            if(!FileUtil.isImageFile(updateDTO.getProfileImage())) {
                throw new Exception400("이미지 파일만 업로드 가능합니다.");
            }
            try {
                String newProfileImageName = FileUtil.saveFile(updateDTO.getProfileImage());
                updateDTO.setProfileImageFileName(newProfileImageName);

                if(oldProfileImage != null && !oldProfileImage.isEmpty()) {
                    FileUtil.deleteFile(oldProfileImage);
                }
            } catch (IOException e) {
                throw new Exception500("파일 저장에 실패했습니다.");
            }
        } else {
            updateDTO.setProfileImageFileName(oldProfileImage);
        }
        String hashPwd = passwordEncoder.encode(updateDTO.getPassword());
        updateDTO.setPassword(hashPwd);
        userEntity.update(updateDTO);

        return new UserResponse.DetailDTO(userEntity);
    }

    @Transactional
    public UserResponse.DetailDTO deleteProfileImage(Long sessionUserId) {
        User userEntity = userRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다."));

        if (!userEntity.isOwner(sessionUserId)) {
            throw new Exception403("삭제 권한이 없습니다.");
        }

        String profileImage = userEntity.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                FileUtil.deleteFile(profileImage);
            } catch (IOException e) {
                throw new Exception500("파일 삭제에 실패했습니다.");
            }
        }
        userEntity.setProfileImage(null);

        return new UserResponse.DetailDTO(userEntity);
    }

    @Transactional
    public void socialJoin(User user) {
        userRepository.save(user);
    }

    // TODO ***
    @Transactional
    public User 포인트충전(Long userId, Integer amount) {
        // 1.  사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("해당 사용자를 조회할 수 없습니다."));

        // 2. 포인트 충전
        user.chargePoint(amount);
        return userRepository.save(user);
    }
}
