package com.ohgireffers.board.model.dto;

import jakarta.validation.constraints.*;

public class BoardRequsetDTO {
    
    //게시글 ID (수정시에만 사용)
    private Long id;
    
    // 게시글 제목
    // 필수 입력
    //최소 1자, 최대 100자
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(min = 1, max = 100, message = "제목은 1자 이상 100자 이하로 입력해주세요.")
    private String title;
    
    // 게시글 내용
    //필수 입력
    //최소 1자, 최대 5000자
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 1, max = 5000, message = "내용은 1자 이상 5000자 이하로 입력해주세요.")
    private String content;
    
    // 작성자
    //필수 입력 (공백만으로는 안됨)
    //최소 2자, 최대 20자
    //특수문자 제한
    @NotBlank(message = "작성자를 입력해주세요.")
    @Size(min = 2, max = 20, message = "작성자는 2자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]+$", message = "작성자는 한글, 영문, 숫자만 입력 가능합니다.")
    private String author;

    // 기본 생성자
    public BoardRequsetDTO() {}

    // 전체 생성자
    public BoardRequsetDTO(Long id, String title, String content, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "BoardRequsetDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
