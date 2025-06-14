'use client';

import { useState, useEffect } from 'react';
// useState 언제 렌더링이 되면서 값이 변경되냐를 설정하기 위해 쓰는 것
// useEffect 랜더링이 완료후 추가적으로 해야할일을 설정하기위해 사용 
import { useRouter, useParams } from 'next/navigation';
// useParams PathVariable처럼 url안에 있는 변수에서 값을 뺴오는데 사용함
// useRouter 페이지 이동을 위해서 사용함 
import BoardForm from '@/components/BoardForm';
import { getboard, updateboard } from '@/apis/board';

export default function Edit() {
    const router = useRouter();
    const params = useParams();
    const { id } = params;

    // [현재_상태_값, 상태_업데이트_함수] = useState(초기값);
    const [board, setBoard] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        //게시글이 랜더링될떄 나 id가 변경될떄 재실행 
        if (id) { 
            // id가 있을떄 해당 코드 실행행
            const getPost = async () => {

                try {
                    const data = await getboard(id);
                    //board 의 id를 가져올떄까지 기다린다음 data에 넣는다
                    setBoard(data);
                    // 새로운 id로 date에 쓴다.
                } catch (error) {
                    //에러가 생기면 에러 메세지와 기본 페이지로 이동 시긴다.
                    console.error('게시글 정보 로딩 실패', error);
                    alert('게시글 정보를 불러오는데 실패했습니다.');
                    router.push('/');
                } finally {
                    setLoading(false);
                    //최종적으로 로딩 상태를 해제한다. 언제나 실행 
                }
            };
            getPost(); //해당 함수 실행 
        }
    }, [id, router]); // 의존성 배열 해당 부분들이 바뀔떄 useEffect가 재 실행된다

    const editPost = async (boardData) => {
        try {
            await updateboard(id, boardData);
            // 해당 함수에 id와 수정할 데이터를 보냄
            alert('게시글이 성공적으로 수정되었습니다.');
            // 성공시 알람과 기본화면으로 이동
            router.push('/');
        } catch (error) {
            console.error('게시글 수정 실패', error);
            // error시 알람과 콘솔 출력
            alert('게시글 수정에 실패했습니다.');
        }
    };

    if (loading) {
        // 로딩이면 해당 출력
        return <div>로딩 중...</div>;
    }

    if (!board) {
        // 게시글이 없으면 출력
        return <div>게시글을 찾을 수 없습니다.</div>;
    }

    return (
        <div>
            {/*boardform의 페이지를 불러오고 onSubmit prop으로 폼 제출 시 실행할 함수를 받음 해당 함수의 버튼이 efitpost로 바뀜뀜*/}
            <h1>게시글 수정</h1>
            <BoardForm board={board} onSubmit={editPost} />
        </div>
    );
} 