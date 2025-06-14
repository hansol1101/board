package com.ohgireffers.board.repository;


import com.ohgireffers.board.model.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findById(Long id);
    //optional 이메서드가 반환하는 결과가 객체를 포함할 수도 있고 포함하지 않을 수도 있는
    // 컨테이너 라는 것을 나타냄
    //null인지 아닌지 자동적으로 확인을 해줌

    Page<Board> findAll(Pageable pageable); // 페이징 지원하는 메서드
}
