package com.ohgireffers.board.model.dto;

import jakarta.validation.constraints.*;

// 댓글 생성/수정 요청 DTO
// Bean Validation을 통한 유효성 검사 포함
public class CommentRequestDTO {
    
    // 댓글 본문
    // - 필수 입력 (공백만으로는 안됨)
    // - 최소 1자, 최대 1000자
    @NotBlank(message = "댓글 내용을 입력해주세요.")
    @Size(min = 1, max = 1000, message = "댓글은 1자 이상 1000자 이하로 입력해주세요.")
    private String textBody;
    
    // 사용자 이름
    // - 필수 입력 (공백만으로는 안됨)
    // - 최소 2자, 최대 20자
    // - 특수문자 제한 (한글, 영문, 숫자만 허용)
    @NotBlank(message = "사용자 이름을 입력해주세요.")
    @Size(min = 2, max = 20, message = "사용자 이름은 2자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]+$", message = "사용자 이름은 한글, 영문, 숫자만 입력 가능합니다.")
    private String user;
    
    // 게시글 ID
    // - 필수 입력
    // - 양수여야 함
    @NotNull(message = "게시글 ID는 필수입니다.")
    @Positive(message = "게시글 ID는 양수여야 합니다.")
    private Long boardId;
    
    // 부모 댓글 ID (대댓글인 경우)
    // - 선택 입력 (null 허용)
    // - 입력시 양수여야 함
    @Positive(message = "부모 댓글 ID는 양수여야 합니다.")
    private Long parentCommentId;

    // 기본 생성자
    public CommentRequestDTO() {}

    // 전체 생성자
    public CommentRequestDTO(String textBody, String user, Long boardId, Long parentCommentId) {
        this.textBody = textBody;
        this.user = user;
        this.boardId = boardId;
        this.parentCommentId = parentCommentId;
    }

    // Getter & Setter
    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    @Override
    public String toString() {
        return "CommentRequestDTO{" +
                "textBody='" + textBody + '\'' +
                ", user='" + user + '\'' +
                ", boardId=" + boardId +
                ", parentCommentId=" + parentCommentId +
                '}';
    }
}
