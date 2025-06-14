'use client';

import { useState, useEffect } from 'react';
import { useRouter, useParams } from 'next/navigation';
import BoardForm from '@/components/BoardForm';
import { getboard, updateboard } from '@/apis/board';

export default function Edit() {
    const router = useRouter();
    const params = useParams();
    const { id } = params;

    const [board, setBoard] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (id) {
            const getPost = async () => {
                try {
                    const data = await getboard(id);
                    setBoard(data);
                } catch (error) {
                    console.error('게시글 정보 로딩 실패', error);
                    alert('게시글 정보를 불러오는데 실패했습니다.');
                    router.push('/');
                } finally {
                    setLoading(false);
                }
            };
            getPost();
        }
    }, [id, router]);

    const editPost = async (boardData) => {
        try {
            await updateboard(id, boardData);
            alert('게시글이 성공적으로 수정되었습니다.');
            router.push('/');
        } catch (error) {
            console.error('게시글 수정 실패', error);
            alert('게시글 수정에 실패했습니다.');
        }
    };

    if (loading) {
        return <div>로딩 중...</div>;
    }

    if (!board) {
        return <div>게시글을 찾을 수 없습니다.</div>;
    }

    return (
        <div>
            <h1>게시글 수정</h1>
            <BoardForm board={board} onSubmit={editPost} />
        </div>
    );
} 