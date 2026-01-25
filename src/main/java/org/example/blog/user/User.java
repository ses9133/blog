package org.example.blog.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blog._core.errors.exception.Exception400;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Table(name = "user_tb")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer point;

    @CreationTimestamp
    private Timestamp createdAt;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'LOCAL'")
    private OAuthProvider provider;

    @Builder
    public User(Long id, String username, String password,
                String email, Integer point, Timestamp createdAt, String profileImage,
                Role role, OAuthProvider provider) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.point = point == null ? 0 : point;
        this.createdAt = createdAt;
        this.profileImage = profileImage;
        this.role = role == null ? Role.USER : role;
        this.provider = provider != null ? provider : OAuthProvider.LOCAL;
    }

    public void update(UserRequest.UpdateDTO updateDTO) {
        updateDTO.validate();
        this.password = updateDTO.getPassword();
        this.profileImage = updateDTO.getProfileImageFileName();
    }

    public boolean isOwner(Long userId) {
        return this.id.equals(userId);
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public String getRoleDisplay() {
        return isAdmin() ? "ADMIN" : "USER";
    }

    public String getProfilePath() {
        if(this.profileImage == null) {
            return null;
        }
        if(this.profileImage.startsWith("http")) {
            return this.profileImage;
        }
        // 아니면 로컬이미지(우리 서버에 저장된) 폴더 경로 붙여서 리턴
        return "/images/" + this.profileImage;
    }

    public boolean isLocal() {
        return this.provider == OAuthProvider.LOCAL;
    }

    public void deductPoint(Integer amount) {
        if(amount == null || amount <= 0) {
            throw new Exception400("차감할 포인트는 0 보다 커야합니다.");
        }
        if(this.point < amount) {
            throw new Exception400("포인트가 부족합니다. 현재 포인트: " + this.point);
        }
        this.point -= amount;
    }

    public void chargePoint(Integer amount) {
        if(amount == null || amount <= 0) {
            throw new Exception400("충전할 포인트는 0 보다 커야합니다.");
        }
        this.point += amount;
    }
}