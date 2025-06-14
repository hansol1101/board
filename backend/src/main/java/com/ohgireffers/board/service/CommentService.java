package com.ohgireffers.board.service;

import com.ohgireffers.board.model.dto.CommentRequestDTO;
import com.ohgireffers.board.model.dto.CommentResponseDTO;
import com.ohgireffers.board.model.entity.Board;
import com.ohgireffers.board.model.entity.Comment;
import com.ohgireffers.board.repository.BoardRepository;
import com.ohgireffers.board.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
    }

    // 댓글 생성
    public CommentResponseDTO createComment(CommentRequestDTO requestDTO) {
        Board board = boardRepository.findById(requestDTO.getBoardId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Comment comment;
        if (requestDTO.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(requestDTO.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("부모 댓글을 찾을 수 없습니다."));
            comment = new Comment(requestDTO.getTextBody(), requestDTO.getUser(), board, parentComment);
        } else {
            comment = new Comment(requestDTO.getTextBody(), requestDTO.getUser(), board);
        }

        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    // 게시글의 모든 댓글 조회 (계층구조)
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByBoardId(Long boardId) {
        List<Comment> allComments = commentRepository.findByBoardIdOrderByCreatedDateAsc(boardId);
        return organizeCommentsHierarchy(allComments);
    }

    // 댓글 수정
    public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO requestDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        comment.setTextBody(requestDTO.getTextBody());
        Comment updatedComment = commentRepository.save(comment);
        return convertToDTO(updatedComment);
    }

    // 댓글 삭제 (물리적 삭제)
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 자식 댓글들이 있는지 확인
        if (!comment.getReplies().isEmpty()) {
            // 자식 댓글들도 함께 삭제 (CASCADE 설정으로 자동 처리됨)
        }
        
        commentRepository.delete(comment);
    }

    // 댓글 개수 조회
    @Transactional(readOnly = true)
    public Long getCommentCountByBoardId(Long boardId) {
        return commentRepository.countByBoardId(boardId);
    }

    // Comment를 DTO로 변환
    private CommentResponseDTO convertToDTO(Comment comment) {

        //
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setTextBody(comment.getTextBody());
        dto.setUser(comment.getUser());
        dto.setBoardId(comment.getBoard().getId());
        dto.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);
        dto.setIsCommentForComment(comment.getIsCommentForComment());
        dto.setDepth(comment.getDepth());
        dto.setOrderNumber(comment.getOrderNumber());
        dto.setCreatedDate(comment.getCreatedDate());
        dto.setModifiedDate(comment.getModifiedDate());
        return dto;
    }

    // 계층구조 정렬 메서드
    private List<CommentResponseDTO> organizeCommentsHierarchy(List<Comment> comments) {
        // 1. 모든 댓글을 DTO로 변환
        List<CommentResponseDTO> allDtos = comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 2. 최상위 댓글만 필터링
        List<CommentResponseDTO> rootComments = allDtos.stream()
                .filter(dto -> dto.getParentCommentId() == null)
                .collect(Collectors.toList());

        // 3. 각 최상위 댓글에 대댓글 연결
        for (CommentResponseDTO rootComment : rootComments) {
            attachReplies(rootComment, allDtos);
        }

        return rootComments;
    }

    // 대댓글 연결 메서드 (재귀)
    private void attachReplies(CommentResponseDTO parentDto, List<CommentResponseDTO> allDtos) {
        List<CommentResponseDTO> replies = allDtos.stream()
                .filter(dto -> parentDto.getId().equals(dto.getParentCommentId()))
                .collect(Collectors.toList());

        parentDto.setReplies(replies);

        // 재귀적으로 대댓글의 대댓글도 처리
        for (CommentResponseDTO reply : replies) {
            attachReplies(reply, allDtos);
        }
    }
}
