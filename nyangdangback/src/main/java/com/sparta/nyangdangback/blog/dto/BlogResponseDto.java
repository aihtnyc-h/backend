package com.sparta.nyangdangback.blog.dto;

import com.sparta.nyangdangback.blog.entity.Blog;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class BlogResponseDto {
    private Long id;
    private String username;
    private String title;
    private String contents;
    private String imageUrl;
    private LocalDateTime createAt;
    private Long likes;

    private boolean heart;

    @Builder
    public BlogResponseDto(Blog blog) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.contents = blog.getContents();
        this.createAt = blog.getCreateAt();
        this.username = blog.getUser().getUsername();
        this.imageUrl=blog.getImageUrl();
        this.likes = blog.getLikes();
    }

    public static BlogResponseDto of(Blog blog) {
        return BlogResponseDto.builder()
                .blog(blog)
                .build();
    }

}

