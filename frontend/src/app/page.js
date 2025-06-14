'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { getallboard, deleteboard } from '@/apis/board';

export default function Home() {
    const [boardList, setBoardList] = useState([]);
    const [currentPage, setCurrentPage] = useState(0); // 백엔드는 보통 0부터 시작
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const postsPerPage = 10;

    const getList = async (page = 0) => {
        try {
            const data = await getallboard(page, postsPerPage);
            console.log('API 응답 데이터:', data);
            
            // Spring Boot Page 응답 구조에 맞게 조정
            setBoardList(data.content || []);
            setTotalPages(data.totalPages || 0);
            setTotalElements(data.totalElements || 0);
            setCurrentPage(data.number || 0);
            
        } catch (error) {
            console.error('게시글 목록 조회 실패:', error);
            setBoardList([]);
            setTotalPages(0);
            setTotalElements(0);
        }
    };

    useEffect(() => {
        getList(0);
    }, []);

    const deletePost = async (id) => {
        if (confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
            try {
                await deleteboard(id);
                alert('게시글이 삭제되었습니다.');
                getList(currentPage); // 현재 페이지 다시 로드
            } catch (error) {
                console.error('게시글 삭제 실패', error);
                alert('게시글 삭제에 실패했습니다.');
            }
        }
    };

    const paginate = (pageNumber) => {
        console.log('페이지 변경:', pageNumber);
        getList(pageNumber);
    };

    // 날짜 포맷팅 함수
    const formatDate = (dateString) => {
        if (!dateString) return '-';
        try {
            return new Date(dateString).toLocaleDateString('ko-KR');
        } catch (error) {
            return '-';
        }
    };

    console.log('현재 페이지:', currentPage);
    console.log('전체 게시글 수:', totalElements);
    console.log('총 페이지 수:', totalPages);
    console.log('현재 페이지 게시글:', boardList.length);

    return (
        <div>
            <h1>게시판</h1>
            <div className="table-header">
                <span>총 {totalElements}개</span>
                <Link href="/write" className="btn btn-primary">게시글 작성</Link>
            </div>
            
            <div className="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>제목</th>
                            <th>작성자</th>
                            <th>날짜</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        {boardList.length > 0 ? (
                            boardList.map((board) => (
                                <tr key={board.id}>
                                    <td>
                                        <Link href={`/board/${board.id}`}>{board.title}</Link>
                                    </td>
                                    <td>{board.author}</td>
                                    <td>
                                        {formatDate(board.createAt || board.postDate || board.createdAt)}
                                    </td>
                                    <td>
                                        <Link href={`/edit/${board.id}`} className="btn">수정</Link>
                                        <button onClick={() => deletePost(board.id)} className="btn btn-danger">삭제</button>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="4">게시글이 없습니다.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>

            {/* 백엔드 페이지네이션 */}
            {totalPages > 1 && (
                <div className="pagination">
                    {/* 이전 페이지 */}
                    {currentPage > 0 && (
                        <button onClick={() => paginate(currentPage - 1)}>
                            이전
                        </button>
                    )}
                    
                    {/* 페이지 번호들 */}
                    {Array.from({ length: totalPages }, (_, i) => i).map(number => (
                        <button 
                            key={number} 
                            onClick={() => paginate(number)} 
                            className={currentPage === number ? 'active' : ''}
                        >
                            {number + 1} {/* 사용자에게는 1부터 표시 */}
                        </button>
                    ))}
                    
                    {/* 다음 페이지 */}
                    {currentPage < totalPages - 1 && (
                        <button onClick={() => paginate(currentPage + 1)}>
                            다음
                        </button>
                    )}
                </div>
            )}
        </div>
    );
}