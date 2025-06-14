package com.ohgireffers.board.service;

import com.ohgireffers.board.model.dto.BoardRequsetDTO;
import com.ohgireffers.board.model.dto.BoardResponseDTO;
import com.ohgireffers.board.model.entity.Board;
import com.ohgireffers.board.repository.BoardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 게시글 관련 비즈니스 로직을 처리하는 서비스 클래스
// 비즈니스 유효성 검사 포함
@Service
public class BoardService {
    
    private static final int MAX_TITLE_LENGTH = 100;
    // 제목 최대 길이
    private static final int MAX_CONTENT_LENGTH = 5000;
    // 내용 최대 길이
    private static final int MAX_AUTHOR_LENGTH = 20;
    // 작성자 최대 길이
    private static final int MAX_PAGE_SIZE = 100;
    // 페이지당 최대 게시글 수
    
    private BoardRepository boardRepository;

    // 의존성 주입을 위해서 사용
    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    // 전체 조회
    public List<BoardResponseDTO> findAllBoards() {
        List<Board> boards = boardRepository.findAll();

        // stream() 메서드를 호출하여 Stream 객체 생성
        // Stream API는 컬렉션의 요소들을 함수형 스타일로 처리할 수 있도록 지원 (원본을 바꾸지 않음)
        // map() 스트림의 각 요소를 다른 형태로 변환하는 역할 (Board -> BoardResponseDTO로 변환)
        // collect() 최종연산으로 스트림의 요소들을 특정 결과 컨테이너로 수집하는 역할
        return boards.stream().map(BoardResponseDTO::new).collect(Collectors.toList());
    }

    // 게시글 생성
    @Transactional
    public BoardResponseDTO createBoard(BoardRequsetDTO boardRequestDTO) {
        // 기본 유효성 검사
        validateBoardRequest(boardRequestDTO);

        Board board = new Board();
        board.setTitle(boardRequestDTO.getTitle());
        board.setContent(boardRequestDTO.getContent());
        board.setAuthor(boardRequestDTO.getAuthor());
        board.setCreateAt(LocalDate.now()); // 생성일시만 설정
        // updatedAt은 실제 수정이 일어날 때만 설정 (생성 시에는 null)
        
        Board savedBoard = boardRepository.save(board);
        return new BoardResponseDTO(savedBoard);
    }

    // 단일 조회
    public BoardResponseDTO getBoard(Long id) {
        // ID 유효성 검사
        validateBoardId(id);
        
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다: " + id));

        // 해당 id에 맞는 데이터를 찾아옴
        // orElseThrow()는 Optional 객체가 제공하는 메서드 중 하나
        // 객체안에 값이 존재하면 그 값을 바로 반환하고 비어있으면 지정된 예외를 발생시킴
       return new BoardResponseDTO(board);
    }

    // 게시글 수정
    @Transactional
    public Board updateboard(Long id, BoardRequsetDTO boardRequsetDTO) {
        // ID 유효성 검사
        validateBoardId(id);
        
        // 수정 요청 데이터 유효성 검사
        validateBoardRequest(boardRequsetDTO);
        
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 게시글을 찾을 수 없습니다: " + id));

        // 기존 게시글 정보 업데이트
        existingBoard.setTitle(boardRequsetDTO.getTitle());
        existingBoard.setAuthor(boardRequsetDTO.getAuthor());
        existingBoard.setContent(boardRequsetDTO.getContent());
        existingBoard.setUpdatedAt(LocalDate.now());
        
        // 해당 값에 새로 요청온 값을 다시 쓰고 저장
        return boardRepository.save(existingBoard);
    }

    // 게시글 삭제
    @Transactional
    public boolean deleteboard(Long id) {
        // ID 유효성 검사
        validateBoardId(id);
        
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 게시글을 찾을 수 없습니다: " + id));

        // 게시글 삭제 전 추가 검증 (예: 댓글이 있는지 확인 등)
        // validateBoardDeletion(existingBoard);
        
        boardRepository.delete(existingBoard);
        return true;
    }

    // 페이징 처리된 게시글 목록 조회
    @Transactional
    public Page<Board> getboardWithPaging(int page, int size) {
        // 페이징 파라미터 유효성 검사
        validatePagingParameters(page, size);
        
        // Pageable: 페이징 및 정렬 정보를 캡슐화하는 인터페이스
        // 이 객체를 repository에 전달하면 자동으로 페이징 쿼리를 생성해줌
        // 가져온 값을 정렬하는데 createAt을 기준으로 내림차순으로 설정
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        
        return boardRepository.findAll(pageable); // 페이징된 결과 반환
    }


    // 게시글 생성/수정 요청의 기본 유효성 검사
    private void validateBoardRequest(BoardRequsetDTO boardRequestDTO) {
        if (boardRequestDTO == null) {
            throw new IllegalArgumentException("게시글 정보가 누락되었습니다.");
        }
        
        // 제목 검사
        if (!StringUtils.hasText(boardRequestDTO.getTitle())) {
            //null이 아니면서, 비어있지 않고, 공백 문자만으로 이루어져 있지도 않은지 확인한다.
            throw new IllegalArgumentException("제목을 입력해주세요.");
        }
        if (boardRequestDTO.getTitle().length() > MAX_TITLE_LENGTH) {
            //
            throw new IllegalArgumentException("제목은 " + MAX_TITLE_LENGTH + "자 이하로 입력해주세요.");
        }
        
        // 내용 검사
        if (!StringUtils.hasText(boardRequestDTO.getContent())) {
            throw new IllegalArgumentException("내용을 입력해주세요.");
        }
        if (boardRequestDTO.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("내용은 " + MAX_CONTENT_LENGTH + "자 이하로 입력해주세요.");
        }
        
        // 작성자 검사
        if (!StringUtils.hasText(boardRequestDTO.getAuthor())) {
            throw new IllegalArgumentException("작성자를 입력해주세요.");
        }
        if (boardRequestDTO.getAuthor().length() > MAX_AUTHOR_LENGTH) {
            throw new IllegalArgumentException("작성자는 " + MAX_AUTHOR_LENGTH + "자 이하로 입력해주세요.");
        }
    }

    // 게시글 ID 유효성 검사
    private void validateBoardId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("올바른 게시글 ID를 입력해주세요.");
        }
    }


    // 페이징 파라미터 유효성 검사
    private void validatePagingParameters(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("페이지 크기는 1 이상이어야 합니다.");
        }
        if (size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("페이지 크기는 " + MAX_PAGE_SIZE + " 이하여야 합니다.");
        }
    }
}
