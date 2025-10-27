/**
 * 세션 만료 시간 관리 유틸리티
 * 5분(300초) 후 자동 만료
 */

const SESSION_EXPIRE_TIME = 3 * 60 * 1000; // 5분 (밀리초)

/**
 * 세션 시작 시간을 저장
 */
export const startSession = () => {
    localStorage.setItem('sessionStartTime', Date.now().toString());
};

/**
 * 세션이 만료되었는지 확인
 */
export const isSessionExpired = () => {
    const startTime = localStorage.getItem('sessionStartTime');
    
    if (!startTime) {
        return true; // 시작 시간이 없으면 만료된 것으로 간주
    }
    
    const elapsed = Date.now() - parseInt(startTime);
    return elapsed >= SESSION_EXPIRE_TIME;
};

/**
 * 남은 세션 시간 반환 (밀리초)
 */
export const getRemainingTime = () => {
    const startTime = localStorage.getItem('sessionStartTime');
    
    if (!startTime) {
        return 0;
    }
    
    const elapsed = Date.now() - parseInt(startTime);
    const remaining = SESSION_EXPIRE_TIME - elapsed;
    
    return remaining > 0 ? remaining : 0;
};

/**
 * 남은 세션 시간을 분:초 형식으로 반환
 */
export const getFormattedRemainingTime = () => {
    const remaining = getRemainingTime();
    const minutes = Math.floor(remaining / 60000);
    const seconds = Math.floor((remaining % 60000) / 1000);
    
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
};

/**
 * 세션 종료
 */
export const endSession = () => {
    localStorage.removeItem('sessionStartTime');
};

/**
 * 세션 연장
 */
export const extendSession = () => {
    startSession();
};

