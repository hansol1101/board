'use client';

import { useRouter } from 'next/navigation';
import BoardForm from '@/components/BoardForm';
import { createboard } from '@/apis/board';


export default function Write() {
    const router = useRouter();
    // 페이지 이동을 위한 변수 설정 

    const addPost = async (boardData) => {
        try {
            await createboard(boardData);
            // createboard에 담은 데이터들을 보냄 
            alert('게시글이 성공적으로 등록되었습니다.');
            //성공시 알람후 기본 페이지로 이동 
            router.push('/');
        } catch (error) {
            console.error('게시글 등록 실패', error);
            // error시 실패 알람과 consol출력 
            alert('게시글 등록에 실패했습니다.');
        }
    };

    return (
        <div>
            <h1>글쓰기</h1>
            <BoardForm onSubmit={addPost} />
            {/*boardform의 페이지를 불러오고 onSubmit prop으로 폼 제출 시 실행할 함수를 받음*/}
        </div>
    );
} 