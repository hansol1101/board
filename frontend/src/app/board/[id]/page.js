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

    const [board, setBoard] = useState(null);
    const [loading, setLoading] = useState(true);
    const [comments, setComments] = useState([]);
    const [commentCount, setCommentCount] = useState(0);
    const [commentForm, setCommentForm] = useState({ text: '', user: '', type: 'new', parentId: null });

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

    const refreshComments = async () => {
        try {
            const [commentsData, countData] = await Promise.all([
                getComments(id), getCommentCount(id)
            ]);
            setComments(commentsData);
            setCommentCount(countData.count);
        } catch (error) {
            console.error('댓글 새로고침 실패:', error);
        }
    };

    useEffect(() => {
        if (!id) return;
        const loadData = async () => {
            try {
                const boardData = await getboard(id);
                setBoard(boardData);
                await refreshComments();
            } catch (error) {
                alert('게시글 정보를 불러오는데 실패했습니다.');
                router.push('/');
            } finally {
                setLoading(false);
            }
        };
        loadData();
    }, [id, router]);

    const handleCommentAction = async (action, commentId = null) => {
        const { text, user, type, parentId } = commentForm;
        if (!text.trim() || (type !== 'edit' && !user.trim())) {
            alert('모든 필드를 입력해주세요.');
            return;
        }

        try {
            const commentData = {
                textBody: text,
                user: type === 'edit' ? "사용자" : user,
                boardId: parseInt(id),
                parentCommentId: type === 'reply' ? parentId : null
            };

            if (action === 'edit') {
                await updateComment(commentId, commentData);
            } else {
                await createComment(commentData);
            }
            
            setCommentForm({ text: '', user: '', type: 'new', parentId: null });
            await refreshComments();
        } catch (error) {
            alert(`${action === 'edit' ? '수정' : '작성'}에 실패했습니다.`);
        }
    };

    const handleDeleteComment = async (commentId) => {
        if (!confirm('댓글을 삭제하시겠습니까?')) return;
        try {
            await deleteComment(commentId);
            await refreshComments();
        } catch (error) {
            alert('댓글 삭제에 실패했습니다.');
        }
    };

    const CommentForm = ({ type, onSubmit, onCancel, initialText = '' }) => (
        <div className={`${type === 'reply' ? 'mt-3 ml-4 p-3 bg-gray-50 rounded-lg' : ''}`}>
            {type !== 'edit' && (
                <input
                    type="text"
                    value={commentForm.user}
                    onChange={(e) => setCommentForm(prev => ({ ...prev, user: e.target.value }))}
                    placeholder="사용자 이름을 입력하세요"
                    className="w-full p-2 mb-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
            )}
            <textarea
                value={commentForm.text}
                onChange={(e) => setCommentForm(prev => ({ ...prev, text: e.target.value }))}
                placeholder={`${type === 'reply' ? '대댓글' : '댓글'}을 작성하세요...`}
                className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                rows={type === 'edit' ? "2" : "3"}
            />
            <div className="mt-2 flex justify-end gap-2">
                {onCancel && (
                    <button onClick={onCancel} className="px-3 py-1 bg-gray-500 text-white rounded hover:bg-gray-600">
                        취소
                    </button>
                )}
                <button onClick={onSubmit} className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600">
                    {type === 'edit' ? '저장' : type === 'reply' ? '대댓글 작성' : '댓글 작성'}
                </button>
            </div>
        </div>
    );

    const CommentItem = ({ comment, level = 0 }) => {
        const isReply = level > 0;
        const isEditing = commentForm.type === 'edit' && commentForm.parentId === comment.id;
        const isReplying = commentForm.type === 'reply' && commentForm.parentId === comment.id;
        
        return (
            <div className={isReply ? 'ml-8 mt-2' : ''}>
                <div className={`border-b border-gray-200 pb-4 ${isReply ? 'border-l-2 border-gray-200 pl-4' : ''}`}>
                    <div className="flex justify-between items-start">
                        <div className="flex-1">
                            <div className="flex items-center gap-2 mb-2">
                                <span className="font-semibold text-gray-800">{comment.user}</span>
                                <span className="text-sm text-gray-500">•</span>
                                <span className="text-sm text-gray-500">
                                    {formatDate(comment.createdDate || comment.createdAt || comment.createAt)}
                                </span>
                            </div>
                            
                            {isEditing ? (
                                <CommentForm
                                    type="edit"
                                    onSubmit={() => handleCommentAction('edit', comment.id)}
                                    onCancel={() => setCommentForm({ text: '', user: '', type: 'new', parentId: null })}
                                    initialText={comment.textBody}
                                />
                            ) : (
                                <p className="mt-2 text-gray-700">{comment.textBody}</p>
                            )}
                            
                            {isReplying && (
                                <CommentForm
                                    type="reply"
                                    onSubmit={() => handleCommentAction('reply')}
                                    onCancel={() => setCommentForm({ text: '', user: '', type: 'new', parentId: null })}
                                />
                            )}
                        </div>
                        
                        <div className="flex gap-2">
                            <button
                                onClick={() => setCommentForm({ text: comment.textBody, user: '', type: 'edit', parentId: comment.id })}
                                className="text-blue-500 hover:text-blue-700"
                            >
                                수정
                            </button>
                            <button
                                onClick={() => setCommentForm({ text: '', user: '', type: 'reply', parentId: comment.id })}
                                className="text-green-500 hover:text-green-700"
                            >
                                대댓글
                            </button>
                            <button
                                onClick={() => handleDeleteComment(comment.id)}
                                className="text-red-500 hover:text-red-700"
                            >
                                삭제
                            </button>
                        </div>
                    </div>
                </div>
                
                {comment.replies?.map(reply => (
                    <CommentItem key={reply.id} comment={reply} level={level + 1} />
                ))}
            </div>
        );
    };

    if (loading) return <div>로딩 중...</div>;
    if (!board) return <div>게시글을 찾을 수 없습니다.</div>;

    return (
        <div className="max-w-4xl mx-auto p-4">
            <h1 className="text-2xl font-bold mb-6">게시글 조회</h1>
            
            <div className="bg-white rounded-lg shadow p-6 mb-8">
                <BoardForm board={board} readOnly />
                
                <div className="flex gap-4 mt-6">
                    <Link href={`/edit/${id}`} className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
                        수정
                    </Link>
                    <Link href="/" className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600">
                        목록으로
                    </Link>
                </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
                <h2 className="text-xl font-bold mb-4">댓글 {commentCount}개</h2>
                
                <div className="mb-6">
                    <CommentForm type="new" onSubmit={() => handleCommentAction('create')} />
                </div>

                <div className="space-y-4">
                    {comments.map(comment => (
                        <CommentItem key={comment.id} comment={comment} />
                    ))}
                </div>
            </div>
        </div>
    );
}