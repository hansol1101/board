package com.ohgireffers.board.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDTO {
    private Long id;
    // 댓글 ID
    private String textBody;
    // 댓글 본문
    private String user;
    // 사용자 이름
    private Long boardId;
    // 게시글 ID
    private Long parentCommentId;
    // 부모 댓글 ID (대댓글인 경우)
    private Boolean isCommentForComment;
    // 대댓글 여부
    private Integer depth;
    // 댓글 깊이
    private Long orderNumber;
    // 댓글 순서
    private LocalDateTime createdDate;
    // 생성일시
    private LocalDateTime modifiedDate;
    // 수정일시
    private List<CommentResponseDTO> replies;
    // 대댓글 목록

    // 기본 생성자
    public CommentResponseDTO() {}

    // 전체 생성자
    public CommentResponseDTO(Long id, String textBody, String user, Long boardId, 
                             Long parentCommentId, Boolean isCommentForComment, 
                             Integer depth, Long orderNumber,
                             LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.textBody = textBody;
        this.user = user;
        this.boardId = boardId;
        this.parentCommentId = parentCommentId;
        this.isCommentForComment = isCommentForComment;
        this.depth = depth;
        this.orderNumber = orderNumber;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getIsCommentForComment() {
        return isCommentForComment;
    }

    public void setIsCommentForComment(Boolean isCommentForComment) {
        this.isCommentForComment = isCommentForComment;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public List<CommentResponseDTO> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentResponseDTO> replies) {
        this.replies = replies;
    }

    @Override
    public String toString() {
        return "CommentResponseDTO{" +
                "id=" + id +
                ", textBody='" + textBody + '\'' +
                ", user='" + user + '\'' +
                ", boardId=" + boardId +
                ", parentCommentId=" + parentCommentId +
                ", isCommentForComment=" + isCommentForComment +
                ", depth=" + depth +
                ", orderNumber=" + orderNumber +
                ", createdDate=" + createdDate +
                ", modifiedDate=" + modifiedDate +
                ", replies=" + replies +
                '}';
    }
}
