package org.example.blog.board;

import lombok.Data;
import org.example.blog._core.utils.DateUtil;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class BoardResponse {
    @Data
    public static class ListDTO {
        private Long id;
        private String title;
        private String username;
        private String createdAt;

        public ListDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            if(board.getUser() != null) {
                this.username = board.getUser().getUsername();
            }
            if(board.getCreatedAt() != null) {
                this.createdAt = DateUtil.format(board.getCreatedAt());
            }
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private String title;
        private String content;
        private Long userId;
        private String username;
        private String createdAt;
        private Boolean premium;
        private Boolean isPurchased;

        public DetailDTO(Board board, Boolean isPurchased) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            if(board.getUser() != null) {
                this.userId = board.getUser().getId();
                this.username = board.getUser().getUsername();
            }
            if(board.getCreatedAt() != null) {
                this.createdAt = DateUtil.format(board.getCreatedAt());
            }
            this.premium = board.getPremium() != null && board.getPremium();
            this.isPurchased = isPurchased != null && isPurchased;
        }
    }

    @Data
    public static class UpdateFormDTO {
        private Long id;
        private String title;
        private String content;
        private Boolean premium;

        public UpdateFormDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.premium = board.getPremium() != null ? board.getPremium() : false;
        }
    }

    @Data
    public static class PageDTO {
        private List<ListDTO> content;
        private int number;
        private int size;
        private int totalPages;
        private Long totalElements;
        private boolean first;
        private boolean last;
        private boolean hasNext;
        private boolean hasPrevious;

        private Integer previousPageNumber;
        private Integer nextPageNumber;

        private List<PageLink> pageLinks;

        public PageDTO(Page<Board> page) {
            page.getContent();
            this.content = page.getContent().stream()
                    .map(ListDTO::new)
                    .toList();
            this.number = page.getNumber();
            this.size = page.getSize();
            this.totalPages = page.getTotalPages();
            this.totalElements = page.getTotalElements();
            this.first  = page.isFirst();
            this.last = page.isLast();
            this.hasNext = page.hasNext();
            this.hasPrevious = page.hasPrevious();

            this.previousPageNumber = page.hasPrevious() ? page.getNumber() : null;
            this.nextPageNumber = page.hasNext() ? page.getNumber() + 2 : null;

            this.pageLinks = generatePageLinks(page);
        }

        private List<PageLink> generatePageLinks(Page<Board> page) {
            List<PageLink> links = new ArrayList<>();
            int currentPage = page.getNumber() + 1;
            int totalPages = page.getTotalPages();

            int startPage = Math.max(1, currentPage - 2);
            int endPage = Math.min(totalPages, currentPage + 2);

            for(int i = startPage; i <= endPage; i++) {
                PageLink link = new PageLink();
                link.setDisplayNumber(i);
                link.setActive(i == currentPage);
                links.add(link);
            }
            return links;
        }
    }

    @Data
    public static class PageLink {
        private int displayNumber;
        private boolean active;
    }
}
