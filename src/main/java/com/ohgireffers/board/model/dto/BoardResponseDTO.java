package com.ohgireffers.board.model.dto;

import com.ohgireffers.board.model.entity.Board;

import java.time.LocalDate;

// 게시글 응답 DTO
// 클라이언트에게 게시글 정보를 전달하기 위한 클래스
public class BoardResponseDTO {
    private Long id;           // 게시글 ID
    private String title;      // 게시글 제목
    private String author;     // 작성자
    private String content;    // 게시글 내용
    private LocalDate createAt;  // 생성일
    private LocalDate updatedAt; // 수정일
    
    // 기본 생성자
    public BoardResponseDTO() {}

    // Board 엔티티를 받아서 DTO로 변환하는 생성자
    public BoardResponseDTO(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.author = board.getAuthor();
        this.content = board.getContent();
        this.createAt = board.getCreateAt();
        this.updatedAt = board.getUpdatedAt();
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "BoardResponseDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", createAt=" + createAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
