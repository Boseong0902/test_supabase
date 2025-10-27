import React, { useState, useEffect } from 'react'
import SignUp from './SignUp'
import Login from './Login'
import Dashboard from './Dashboard'
import Message from './pages/Message'
import { supabase } from './supabaseClient'
import { endSession } from './utils/sessionManager'


function App() {
    const [page, setPage] = useState('signup')
    const [isLoggedIn, setIsLoggedIn] = useState(false)

    // 로그인 상태 확인
    useEffect(() => {
        const checkSession = async () => {
            const { data: { session } } = await supabase.auth.getSession()
            setIsLoggedIn(!!session)
        }
        checkSession()
    }, [page])

    // 로그아웃 처리
    const handleLogout = async () => {
        await supabase.auth.signOut()
        endSession() // 세션 만료 시간도 초기화
        setIsLoggedIn(false)
        setPage('login')
    }

    return (
        <div style={{ textAlign: 'center', marginTop: 50 }}>
            <div>
                <button onClick={() => setPage('signup')}>회원가입</button>
                {isLoggedIn ? (
                    <button onClick={handleLogout} style={{ backgroundColor: '#ff4444', color: 'white' }}>
                        로그아웃
                    </button>
                ) : (
                    <button onClick={() => setPage('login')}>로그인</button>
                )}
                <button onClick={() => setPage('dashboard')}>대시보드</button>
                <button onClick={() => setPage('message')}>메시지</button>
            </div>

            {page === 'signup' && <SignUp onLogin={() => setIsLoggedIn(true)} />}
            {page === 'login' && <Login onLogin={() => setIsLoggedIn(true)} />}
            {page === 'dashboard' && <Dashboard setPage={setPage} />}
            {page === 'message' && <Message setPage={setPage} />}
        </div>
    )
}

export default App