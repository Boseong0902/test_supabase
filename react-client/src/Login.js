import React, { useState } from 'react'
import { supabase } from './supabaseClient'

export default function Login() {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [token, setToken] = useState('')
    const [message, setMessage] = useState('')

    const handleLogin = async (e) => {
        e.preventDefault()
        const { data, error } = await supabase.auth.signInWithPassword({ email, password })
        if (error) setMessage(`❌ ${error.message}`)
        else {
            setToken(data.session.access_token)
            setMessage('✅ 로그인 성공!')
            console.log('Access Token:', data.session.access_token)
        }
    }

    return (
        <div style={{ marginTop: 50 }}>
            <h2>로그인</h2>
            <form onSubmit={handleLogin}>
                <input
                    type="email"
                    placeholder="이메일"
                    onChange={(e) => setEmail(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="비밀번호"
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button type="submit">로그인</button>
            </form>
            <p>{message}</p>
            {token && <pre>{token}</pre>}
        </div>
    )
}