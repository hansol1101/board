package com.ohgireffers.board.controller;

import com.ohgireffers.board.model.dto.BoardRequsetDTO;
import com.ohgireffers.board.model.dto.BoardResponseDTO;
import com.ohgireffers.board.model.entity.Board;
import com.ohgireffers.board.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// 게시글 관련 REST API 컨트롤러
// Bean Validation을 통한 요청 데이터 유효성 검사 포함
@RestController
@RequestMapping("/board")
public class BoardController {

    private BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 단일 조회 - ID를 이용해서 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> findboardbyid(@PathVariable Long id) {
        // Path Variable 유효성 검사
        if (id == null || id <= 0) {
            return new ResponseEntity<>("올바른 게시글 ID를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        try {
            BoardResponseDTO board = boardService.getBoard(id);
            return new ResponseEntity<>(board, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("게시글 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게시글 등록
    @PostMapping("/create")
    public ResponseEntity<?> createBoard(@Valid @RequestBody BoardRequsetDTO boardRequsetDTO,
                                        BindingResult bindingResult) {
        // 유효성 검사 실패시 에러 메시지 반환
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
        }

        try {
            BoardResponseDTO board = boardService.createBoard(boardRequsetDTO);
            return new ResponseEntity<>(board, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("게시글 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoard(@PathVariable Long id, 
                                        @Valid @RequestBody BoardRequsetDTO boardRequsetDTO,
                                        BindingResult bindingResult) {
        // Path Variable 유효성 검사
        if (id == null || id <= 0) {
            return new ResponseEntity<>("올바른 게시글 ID를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        // DTO 유효성 검사 실패시 에러 메시지 반환
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
        }

        try {
            Board board = boardService.updateboard(id, boardRequsetDTO);
            if (board == null) {
                return new ResponseEntity<>("수정할 게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(board, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("게시글 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long id) {
        // Path Variable 유효성 검사
        if (id == null || id <= 0) {
            return new ResponseEntity<>("올바른 게시글 ID를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        try {
            boolean deleted = boardService.deleteboard(id);
            if (!deleted) {
                return new ResponseEntity<>("삭제할 게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("게시글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("게시글 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 페이징 처리된 게시글 목록 조회
    @GetMapping("/")
    public ResponseEntity<?> getpages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // 페이징 파라미터 유효성 검사
        if (page < 0) {
            return new ResponseEntity<>("페이지 번호는 0 이상이어야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (size <= 0 || size > 100) {
            return new ResponseEntity<>("페이지 크기는 1 이상 100 이하여야 합니다.", HttpStatus.BAD_REQUEST);
        }

        try {
            // page는 페이지번호 (0부터 시작)
            // size는 한 페이지당 몇개의 데이터를 보여줄지 정하는 것
            // defaultValue는 값이 없다면 자동으로 해당 값을 넣어서 넘김
            Page<Board> boardPage = boardService.getboardWithPaging(page, size);

            // Board 엔티티를 BoardResponseDTO로 변환
            Page<BoardResponseDTO> dtoPage = boardPage.map(BoardResponseDTO::new);

            return ResponseEntity.ok(dtoPage);
        } catch (Exception e) {
            return new ResponseEntity<>("게시글 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
