'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';

export default function BoardForm({ board = null, onSubmit, readOnly = false }) {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [author, setAuthor] = useState('');
    // 제목, 내용, 작성자를 state로 담음 

    useEffect(() => {
        if (board) {
            // 게시글이 있다면 
            setTitle(board.title || '');
            setContent(board.content || '');
            setAuthor(board.author || '');
            // 해당 제목, 내용 작성자가 없다면 빈 배열로 설정함
        }
    }, [board]);
    //board가 변경될떄 해당 useEffect 실행행
    const formatDate = (dateString) => {
        if (!dateString) return '-';
        try {
            const date = new Date(dateString);
            if (isNaN(date.getTime())) return '-';
            return new Date(date.getTime() + (9 * 60 * 60 * 1000))
                .toLocaleString('ko-KR', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: false
                });
        } catch {
            return '-';
        }
    };

    const submitForm = (e) => {
        e.preventDefault();
        //페이지 새로고침 방지 
        if (!title || !content || !author) {
            alert('제목, 내용, 작성자를 모두 입력해주세요.');
            //비어있는지 확인을 진행함 
            return;
        }
        onSubmit({ title, content, author });
        //모두 입력이 되어 있으면 부모 컴포넌트의 onsubmit함수 호출 
    };

    if (readOnly) {
        return (
            <div className="space-y-4">
                <div>
                    <label className="font-semibold">제목</label>
                    <p className="mt-1">{title}</p>
                </div>
                <div>
                    <label className="font-semibold">작성자</label>
                    <p className="mt-1">{author}</p>
                </div>
                <div>
                    <label className="font-semibold">작성일</label>
                    <p className="mt-1">{formatDate(board?.createAt)}</p>
                </div>
                <div>
                    <label className="font-semibold">내용</label>
                    <div className="mt-1 p-4 bg-gray-50 rounded min-h-[150px] whitespace-pre-wrap">
                        {content}
                    </div>
                </div>
            </div>
        );
    }

    return (
        <form onSubmit={submitForm}>
            {/*폼이 제출될 떄 실행할 함수 엔터키를 누르거나 submit 버튼 클릭시 실행 submitForm 함수 호출  */}
            <div className="form-group">
                <label htmlFor="author">작성자</label>
                <input
                    type="text"
                    id="author"
                    className="form-control"
                    value={author}
                    //
                    onChange={(e) => setAuthor(e.target.value)}
                    //
                    placeholder="작성자 이름을 입력하세요"
                    // 입력전 안내 텍스트 
                />
            </div>
            <div className="form-group">
                <label htmlFor="title">제목</label>
                <input
                    type="text"
                    id="title"
                    className="form-control"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    placeholder="제목을 입력하세요"
                    //
                />
            </div>
            <div className="form-group">
                <label htmlFor="content">내용</label>
                <textarea
                    id="content"
                    className="form-control"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    placeholder="내용을 입력하세요"
                ></textarea>
            </div>
            <div className="form-actions">
                <Link href="/" className="btn">취소</Link>
                <button type="submit" className="btn btn-primary">
                    {board ? '수정' : '작성'}
                </button>
            </div>
        </form>
    );
}