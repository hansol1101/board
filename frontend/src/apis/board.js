import axios from "axios";// http 요청을 보낼 수 있게 도와주는 외부 라이브러리 
const API_URL = "http://localhost:8080/board"; // 백엔드 API 주소를 변수에 넣어둠 
const COMMENT_URL = "http://localhost:8080/api/comments";
const BOARD_COMMENT_URL = "http://localhost:8080/api/boards";

// 전체 게시글 조회 (페이징 지원)
export const getallboard = async (page = 0, size = 10) => {
    // 비동기 함수를 만듬 
    try {
        const response = await axios.get(`${API_URL}/?page=${page}&size=${size}`);
        // 해당 url로 요청을 보내고 값이 오기까지 기다린다음 변수에 담고 리턴함
        return response.data;
    } catch (error) {
        // 에러뜨면 에러코드 출력력
        console.error("Error fetching all boards:", error);
        throw error;
    }
};

//상세 조회
export const getboard = async (id) => {
    try {
        const response = await axios.get(`${API_URL}/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching board with id ${id}:`, error);
        throw error;
    }
};

//생성
export const createboard = async (boardData) => {
    try {
        const response = await axios.post(`${API_URL}/create`, boardData);
        return response.data;
    } catch (error) {
        console.error("Error creating board:", error);
        throw error;
    }
};

//수정
export const updateboard = async (id, boardData) => {
    try {
        const response = await axios.put(`${API_URL}/${id}`, boardData);
        return response.data;
    } catch (error) {
        console.error(`Error updating board with id ${id}:`, error);
        throw error;
    }
};

//삭제
export const deleteboard = async (id) => {
    try {
        await axios.delete(`${API_URL}/${id}`);
    } catch (error) {
        console.error(`Error deleting board with id ${id}:`, error);
        throw error;
    }
};

// 댓글 목록 조회
export const getComments = async (boardId) => {
    try {
        const response = await axios.get(`${COMMENT_URL}/board/${boardId}`);
        return response.data;
    } catch (error) {
        console.error(`게시글 ${boardId}의 댓글 조회 실패:`, error);
        return []; // 에러 발생 시 빈 배열 반환
    }
};

// 댓글 개수 조회
export const getCommentCount = async (boardId) => {
    try {
        const response = await axios.get(`${COMMENT_URL}/board/${boardId}/count`);
        return { count: response.data }; // 백엔드가 Long 타입으로 반환하므로 객체로 변환
    } catch (error) {
        console.error(`게시글 ${boardId}의 댓글 개수 조회 실패:`, error);
        return { count: 0 }; // 에러 발생 시 0 반환
    }
};

// 댓글 작성
export const createComment = async (commentData) => {
    try {
        const response = await axios.post(COMMENT_URL, commentData);
        return response.data;
    } catch (error) {
        console.error("댓글 작성 실패:", error);
        throw error;
    }
};

// 대댓글 작성
export const createReply = async (parentCommentId, commentData) => {
    try {
        const response = await axios.post(`${COMMENT_URL}/${parentCommentId}/reply`, commentData);
        return response.data;
    } catch (error) {
        console.error(`대댓글 작성 실패:`, error);
        throw error;
    }
};

// 댓글 수정
export const updateComment = async (commentId, commentData) => {
    try {
        const response = await axios.put(`${COMMENT_URL}/${commentId}`, commentData);
        return response.data;
    } catch (error) {
        console.error(`댓글 ${commentId} 수정 실패:`, error);
        throw error;
    }
};

// 댓글 삭제
export const deleteComment = async (commentId) => {
    try {
        await axios.delete(`${COMMENT_URL}/${commentId}`);
    } catch (error) {
        console.error(`댓글 ${commentId} 삭제 실패:`, error);
        throw error;
    }
}; 