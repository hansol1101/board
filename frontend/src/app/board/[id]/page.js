'use client';

import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { getboard, getComments, getCommentCount, createComment, updateComment, deleteComment } from '@/apis/board';
import Link from 'next/link';
import BoardForm from '@/components/BoardForm';

export default function Detail() {
    const params = useParams();
    const router = useRouter();
    const { id } = params;
    // URL 파라미터에서 게시글 ID를 추출합니다.

    const [board, setBoard] = useState(null); // 현재 보고 있는 게시글 데이터를 저장
    const [loading, setLoading] = useState(true); // 데이터 로딩 상태를 나타냅니다. 로딩 중 true, 완료시 false
    const [comments, setComments] = useState([]); // 현재 게시글에 달린 댓글 목록을 저장
    const [commentCount, setCommentCount] = useState(0); // 댓글의 총 개수를 저장
    const [commentForm, setCommentForm] = useState({ text: '', user: '', type: 'new', parentId: null });
      // 댓글 입력 폼의 상태를 관리합니다.


    // 날짜 형식을 변환하는 함수 한국 일시로 바꿔줌 
    const formatDate = (dateString) => {
        if (!dateString) return '-';
        try {
            const date = new Date(dateString);
            return isNaN(date.getTime()) ? '-' : 
                new Date(date.getTime() + (9 * 60 * 60 * 1000))
                    .toLocaleString('ko-KR', {
                        year: 'numeric', month: '2-digit', day: '2-digit',
                        hour: '2-digit', minute: '2-digit', hour12: false
                    });
        } catch { return '-'; }
    };


    // 댓글 목록을 새로고침하는 함수 
    const refreshComments = async () => {
        try {
            const [commentsData, countData] = await Promise.all([
                 // 게시글 ID를 사용하여 댓글 목록과 댓글 개수를 동시에 가져옴옴
                //병렬로 동시에 실행해서모든 작업이 완료될 때까지 기다림
                getComments(id), getCommentCount(id)
            ]);
            setComments(commentsData); // 댓글 목록을 새로씀
            setCommentCount(countData.count); // 댓글 개수를 새로씀씀
        } catch (error) {
            //error시 해당 콘솔 출력력
            console.error('댓글 새로고침 실패:', error);
        }
    };

    // 게시글 정보를 불러오는 함수 
    useEffect(() => {
        if (!id) return;
        // id가 없다면 해당 함수 종료
        const loadData = async () => {
            try {
                const boardData = await getboard(id); // 게시글 데이터를 가져옴
                setBoard(boardData); // 게시글 데이터를 상태에 저장
                await refreshComments(); // 댓글 목록을 새로고침
            } catch (error) {
                alert('게시글 정보를 불러오는데 실패했습니다.'); // 에러 메시지 표시
                router.push('/'); // 기본 페이지로 이동
            } finally {
                setLoading(false); // 로딩 상태를 false로 설정
            }
        };
        loadData();
    }, [id, router]); //id 또는 경로(router가 변경될때마다) 해당 함수 실행

    const handleCommentAction = async (action, commentId = null) => {
        // 댓글 작성 또는 수정 작업을 처리하는 함수
        const { text, user, type, parentId } = commentForm;
        if (!text.trim() || (type !== 'edit' && !user.trim())) {
            alert('모든 필드를 입력해주세요.'); // 모든 필드를 입력하지 않았을 때 알림 표시
            return;
        }

        try {
            // 댓글 데이터를 생성하는 객체
            const commentData = {
                textBody: text, // 댓글 내용
                user: type === 'edit' ? "사용자" : user, // 댓글 작성자
                boardId: parseInt(id), // 게시글 ID
                parentCommentId: type === 'reply' ? parentId : null // 대댓글 일때 부모 댓글 ID, 아니면 null
            };

            if (action === 'edit') { // 수정 작업일때
                await updateComment(commentId, commentData); // 댓글 데이터를 수정
            } else { // 작성 작업일때
                await createComment(commentData); // 댓글 데이터를 생성
            }
            
            setCommentForm({ text: '', user: '', type: 'new', parentId: null }); // 댓글 입력 폼을 초기화
            await refreshComments(); // 댓글 목록을 새로고침
        } catch (error) {
            alert(`${action === 'edit' ? '수정' : '작성'}에 실패했습니다.`); // 에러 메시지 표시
        }
    };

    // 댓글 삭제 작업을 처리하는 함수
    const handleDeleteComment = async (commentId) => {
        if (!confirm('댓글을 삭제하시겠습니까?')) return; // 삭제 확인 대화 상자 표시
        try {
            await deleteComment(commentId); // 댓글 데이터를 삭제
            await refreshComments(); // 댓글 목록을 새로고침
        } catch (error) {
            alert('댓글 삭제에 실패했습니다.');
        }
    };



    // 댓글 입력 폼을 관리하는 함수
    const CommentForm = ({ type, onSubmit, onCancel, initialText = '' }) => (
        <div className={`${type === 'reply' ? 'mt-3 ml-4 p-3 bg-gray-50 rounded-lg' : ''}`}>
            {type !== 'edit' && ( // 수정 작업이 아닐때 해당 코드 실행
                <input
                    type="text"
                    value={commentForm.user} // 댓글 작성자 이름
                    onChange={(e) => setCommentForm(prev => ({ ...prev, user: e.target.value }))} // 댓글 작성자 이름 변경
                    placeholder="사용자 이름을 입력하세요"
                    className="w-full p-2 mb-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
            )}
            {/* 댓글 내용 입력 필드 */}
            <textarea
                value={commentForm.text} // 댓글 내용
                onChange={(e) => setCommentForm(prev => ({ ...prev, text: e.target.value }))} // 댓글 내용 변경
                placeholder={`${type === 'reply' ? '대댓글' : '댓글'}을 작성하세요...`}
                className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                rows={type === 'edit' ? "2" : "3"}
            />

            <div className="mt-2 flex justify-end gap-2">
                {onCancel && ( // 취소 버튼이 있을때 해당 코드 실행
                    <button onClick={onCancel} className="px-3 py-1 bg-gray-500 text-white rounded hover:bg-gray-600">
                        // 취소 버튼 클릭시 해당 함수 실행
                        취소
                    </button>
                )}
                {/* 댓글 작성 버튼 */}
                <button onClick={onSubmit} className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600">
                    {type === 'edit' ? '저장' : type === 'reply' ? '대댓글 작성' : '댓글 작성'}
                </button>
            </div>
        </div>
    );

    // 댓글 항목을 표시하는 함수
    const CommentItem = ({ comment, level = 0 }) => {
        const isReply = level > 0; // 대댓글 일때 true
        const isEditing = commentForm.type === 'edit' && commentForm.parentId === comment.id; // 수정 작업일때 true
        const isReplying = commentForm.type === 'reply' && commentForm.parentId === comment.id; // 대댓글 작업일때 true
        
        return (
            // 대댓글 일때 왼쪽 여백 추가
            <div className={isReply ? 'ml-8 mt-2' : ''}>
                // 대댓글 일때 왼쪽 테두리 추가
                <div className={`border-b border-gray-200 pb-4 ${isReply ? 'border-l-2 border-gray-200 pl-4' : ''}`}>
                    // 댓글 정보와 버튼 영역
                    <div className="flex justify-between items-start">
                        // 댓글 정보 영역
                        <div className="flex-1">
                            <div className="flex items-center gap-2 mb-2">
                                <span className="font-semibold text-gray-800">{comment.user}</span>
                                <span className="text-sm text-gray-500">•</span>
                                <span className="text-sm text-gray-500">
                                    // 댓글 작성일 표시
                                    {formatDate(comment.createdDate || comment.createdAt || comment.createAt)}
                                </span>
                            </div>
                            // 수정 작업일때 해당 코드 실행
                            {isEditing ? (
                                <CommentForm
                                    type="edit"
                                    onSubmit={() => handleCommentAction('edit', comment.id)}
                                    onCancel={() => setCommentForm({ text: '', user: '', type: 'new', parentId: null })}
                                    // 댓글 입력 폼을 초기화
                                    initialText={comment.textBody}
                                    // 댓글 내용 초기화
                                />
                            ) : (
                                // 댓글 내용 표시
                                <p className="mt-2 text-gray-700">{comment.textBody}</p>
                            )}
                            
                            {isReplying && (
                                <CommentForm
                                    type="reply"
                                    onSubmit={() => handleCommentAction('reply')}
                                    // 대댓글 작성 버튼 클릭시 해당 함수 실행
                                    onCancel={() => setCommentForm({ text: '', user: '', type: 'new', parentId: null })}
                                    // 대댓글 입력 폼을 초기화
                                />
                            )}
                        </div>
                        
                        <div className="flex gap-2">
                            <button
                                onClick={() => setCommentForm({ text: comment.textBody, user: '', type: 'edit', parentId: comment.id })}
                                className="text-blue-500 hover:text-blue-700"
                                // 수정 버튼 클릭시 해당 함수 실행
                            >
                                수정
                            </button>
                            <button
                                onClick={() => setCommentForm({ text: '', user: '', type: 'reply', parentId: comment.id })}
                                className="text-green-500 hover:text-green-700"
                                // 대댓글 버튼 클릭시 해당 함수 실행
                            >
                                대댓글
                            </button>
                            <button
                                onClick={() => handleDeleteComment(comment.id)}
                                // 댓글 삭제 버튼 클릭시 해당 함수 실행
                                className="text-red-500 hover:text-red-700"
                            >
                                삭제
                            </button>
                        </div>
                    </div>
                </div>
                
                {comment.replies?.map(reply => (
                    // 대댓글 항목을 표시하는 함수
                    <CommentItem key={reply.id} comment={reply} level={level + 1} />
                ))}
            </div>
        );
    };

    if (loading) return <div>로딩 중...</div>;
    // 로딩 중일때 해당 코드 실행
    if (!board) return <div>게시글을 찾을 수 없습니다.</div>;
    // 게시글이 없을때 해당 코드 실행

    return (
        // 게시글 상세 페이지 렌더링
        <div className="max-w-4xl mx-auto p-4">
            <h1 className="text-2xl font-bold mb-6">게시글 조회</h1>
            // 게시글 정보 영역
            <div className="bg-white rounded-lg shadow p-6 mb-8">
                <BoardForm board={board} readOnly />
                // 게시글 정보 표시 BoardForm의 컴포넌트를 받아옴 
                <div className="flex gap-4 mt-6">
                    <Link href={`/edit/${id}`} className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
                        수정 
                        // 수정 버튼 클릭시 해당 id의 수정 페이지로 이동 
                    </Link>
                    <Link href="/" className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600">
                        목록으로
                        // 목록으로 버튼 클릭시 기본 페이지로 이동 
                    </Link>
                </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
                <h2 className="text-xl font-bold mb-4">댓글 {commentCount}개</h2>
                // 댓글 개수 표시
                
                <div className="mb-6">
                    <CommentForm type="new" onSubmit={() => handleCommentAction('create')} />
                        // 댓글 작성 버튼 클릭시 댓글 작성하는 함수 실행
                </div>
                // 댓글 입력 폼 영역
                <div className="space-y-4">
                    {comments.map(comment => (
                        <CommentItem key={comment.id} comment={comment} />
                        // 댓글 항목을 표시하는 함수
                    ))}
                </div>
            </div>
        </div>
    );
}