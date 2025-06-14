package com.ohgireffers.board.controller;

import com.ohgireffers.board.model.dto.CommentRequestDTO;
import com.ohgireffers.board.model.dto.CommentResponseDTO;
import com.ohgireffers.board.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// 댓글 관련 REST API 컨트롤러
// Bean Validation을 통한 요청 데이터 유효성 검사 포함
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 생성 (일반 댓글 및 대댓글)
    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequestDTO requestDTO, BindingResult bindingResult) {
        // bindongresult는 유효성 검사를 수행한 결과가 들어있는 객체이다.
        //유효성 검사 실패시 에러 메시지 반환
        if (bindingResult.hasErrors()) {
            //오류가 있는지 확인을하고 boolen으로 반환 만약 오류 발생시 아래 코드 실행
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    // bindingresult 객체에서 발생한 보는 filederrors를 Stream API로 반환
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    //스트림의 각 요소를 다른 형태의 요소로 반환 error라는 변수를 받아서 이름과 디테일 메시지를 해당 형태로 만듬
                    .collect(Collectors.toList());
            // 스트림의 모든 요소를 list로 만듬
            //
            return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
        }

        try {
            //전달받은 댓글 또는 대댓글을 createComment로 넘긴
            CommentResponseDTO responseDTO = commentService.createComment(requestDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            //오류가 생긴다면 오류 반환
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 게시글의 모든 댓글 조회
    @GetMapping("/board/{boardId}")
    public ResponseEntity<?> getCommentsByBoardId(@PathVariable Long boardId) {
        // 간단한 유효성 검사
        if (boardId == null || boardId <= 0) {
            return new ResponseEntity<>("올바른 게시글 ID를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        try {
            List<CommentResponseDTO> comments = commentService.getCommentsByBoardId(boardId);
            //특정 게시글의 댓글을 조회하는 service에 해당 게시글 id를 보냄
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, 
                                          @Valid @RequestBody CommentRequestDTO requestDTO,
                                          BindingResult bindingResult) {
        // Path Variable 유효성 검사
        if (commentId == null || commentId <= 0) {
            return new ResponseEntity<>("올바른 댓글 ID를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        //유효성 검사 실패시 에러 메시지 반환
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
        }

        try {
            CommentResponseDTO responseDTO = commentService.updateComment(commentId, requestDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        // 간단한 유효성 검사
        if (commentId == null || commentId <= 0) {
            return new ResponseEntity<>("올바른 댓글 ID를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        try {
            commentService.deleteComment(commentId);
            return new ResponseEntity<>("댓글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 게시글의 댓글 개수 조회
    @GetMapping("/board/{boardId}/count")
    public ResponseEntity<?> getCommentCountByBoardId(@PathVariable Long boardId) {
        // 간단한 유효성 검사
        if (boardId == null || boardId <= 0) {
            return new ResponseEntity<>("올바른 게시글 ID를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        try {
            Long count = commentService.getCommentCountByBoardId(boardId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 개수 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 대댓글 생성
    @PostMapping("/{commentId}/reply")
    public ResponseEntity<?> createReply(@PathVariable Long commentId,
                                        @Valid @RequestBody CommentRequestDTO requestDTO,
                                        BindingResult bindingResult) {
        // Path Variable 유효성 검사
        if (commentId == null || commentId <= 0) {
            return new ResponseEntity<>("올바른 부모 댓글 ID를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        // DTO 유효성 검사 실패시 에러 메시지 반환
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
        }

        try {
            // 부모 댓글 ID 설정
            requestDTO.setParentCommentId(commentId);
            CommentResponseDTO responseDTO = commentService.createComment(requestDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("대댓글 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
