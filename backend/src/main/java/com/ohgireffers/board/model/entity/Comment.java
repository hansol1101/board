package com.ohgireffers.board.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment")
public class Comment extends JpaBaseTimeEntity {
    // 엔티티의 생성 시간과 수정시간을 자동으로 관리해 주는 추상 클래스
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    // 댓글 id

    @Column(length = 1000, nullable = false)
    private String textBody;
    // 댓글 본문

    @Column(name = "user", nullable = false)
    private String user; // 사용자 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
    // 게시글 참조키 1:n관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment; // 부모 댓글

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>(); // 대댓글 목록



    @Column(name = "is_comment_for_comment")
    private Boolean isCommentForComment = false; // 대댓글 여부



    @Column(name = "depth")
    private Integer depth = 0; // 댓글의 깊이 -> 몇개의 댓글이 달렸는지

    @Column(name = "order_number")
    private Long orderNumber; // 댓글의 순서

    // 기본 생성자
    public Comment() {}

    // 생성자
    public Comment(String textBody, String user, Board board) {
        this.textBody = textBody;
        this.user = user;
        this.board = board;
        this.isCommentForComment = false;
        this.depth = 0;
    }

    // 대댓글 생성자
    public Comment(String textBody, String user, Board board, Comment parentComment) {
        this.textBody = textBody;
        this.user = user;
        this.board = board;
        this.parentComment = parentComment;
        this.isCommentForComment = true;
        this.depth = parentComment.getDepth() + 1;
    }

    // 대댓글 추가 메서드
    public void addReply(Comment reply) {
        this.replies.add(reply);
        reply.setParentComment(this);
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

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
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

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", textBody='" + textBody + '\'' +
                ", user='" + user + '\'' +
                ", isCommentForComment=" + isCommentForComment +
                ", depth=" + depth +
                ", orderNumber=" + orderNumber +
                '}';
    }
}
