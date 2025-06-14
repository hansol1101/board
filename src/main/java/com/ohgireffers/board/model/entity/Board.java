package com.ohgireffers.board.model.entity;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity //db의 테이블과 메핑을 시킴
@Table(name = "board")
// 데이터베이스를 명시적으로 지정하기 위해서 사용함 엔티티만 쓰면 클래스 이름이 테이블 명으로 된다
public class Board {


    @Id //기본키를 지정하고 해당 값은 자동 생성되게 바꿈
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //db의 컬럼과 매핑한다.
    @Column(name = "title")
    private String title;


    @Column(name = "author")
    private String author;

    @Column(name = "contents")
    private String content;

    @Column(name = "created_at")
    private LocalDate createAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    //db에서 값을 읽어와 엔티티 객체를 만들떄 기본생성자를 호출해서 객체를 먼저 생성한 뒤 각 필드에 값을 주입한다.
    // 만약 기본 생성자가 없으면 JPA가 객체를 만들 수 없어 예외가 발생합니다.
    public Board() {}


    //객체가 생성될 때 한 번 자동으로 호출되어 객체의 필드(멤버 변수)나
    // 상태를 원하는 값으로 초기화
    public Board(Long id, String title, String author, String content, LocalDate createAt, LocalDate updatedAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.createAt = createAt;
        this.updatedAt = updatedAt;
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
        return "Board{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", createAt=" + createAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}