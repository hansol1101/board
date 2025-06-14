package com.ohgireffers.board.repository;

import com.ohgireffers.board.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 특정 게시글의 모든 댓글 조회 (생성일시 순 정렬)
    List<Comment> findByBoardIdOrderByCreatedDateAsc(Long boardId);
    
    // 특정 게시글의 댓글 개수 조회
    Long countByBoardId(Long boardId);
}
