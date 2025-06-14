'use client';

import { useRouter } from 'next/navigation';
import BoardForm from '@/components/BoardForm';
import { createboard } from '@/apis/board';

export default function Write() {
    const router = useRouter();

    const addPost = async (boardData) => {
        try {
            await createboard(boardData);
            alert('게시글이 성공적으로 등록되었습니다.');
            router.push('/');
        } catch (error) {
            console.error('게시글 등록 실패', error);
            alert('게시글 등록에 실패했습니다.');
        }
    };

    return (
        <div>
            <h1>글쓰기</h1>
            <BoardForm onSubmit={addPost} />
        </div>
    );
} 