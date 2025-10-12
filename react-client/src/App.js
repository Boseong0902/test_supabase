import React, { useState } from 'react'
import SignUp from './SignUp'
import Login from './Login'
import Dashboard from './Dashboard'
import Message from './pages/Message'   // ✅ 추가


function App() {
    const [page, setPage] = useState('signup')

    return (
        <div style={{ textAlign: 'center', marginTop: 50 }}>
            <div>
                <button onClick={() => setPage('signup')}>회원가입</button>
                <button onClick={() => setPage('login')}>로그인</button>
                <button onClick={() => setPage('dashboard')}>대시보드</button>
                <button onClick={() => setPage('message')}>메시지</button>
            </div>

            {page === 'signup' && <SignUp />}
            {page === 'login' && <Login />}
            {page === 'dashboard' && <Dashboard />}
            {page === 'message' && <Message />}
        </div>
    )
}

export default App