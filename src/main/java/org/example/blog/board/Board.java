package org.example.blog.board;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.blog.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@Table(name = "board_tb")
@Entity
@Data
public class Board {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Boolean premium = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Builder
    public Board(String title, String content, Boolean premium, User user) {
        this.title = title;
        this.content = content;
        this.premium = premium != null && premium;
        this.user = user;
    }

    public void update(BoardRequest.UpdateDTO updateDTO) {
        updateDTO.validate();

        this.title = updateDTO.getTitle();
        this.content = updateDTO.getContent();
        this.premium = updateDTO.getPremium() != null && updateDTO.getPremium();
    }

    public boolean isOwner(Long userId) {
        return this.user.getId().equals(userId);
    }

    public void updateTitle(String newTitle) {
        if(newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        this.title = newTitle;
    }

    public void updateContent(String newContent) {
        if(newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }
        this.content = newContent;
    }
}
