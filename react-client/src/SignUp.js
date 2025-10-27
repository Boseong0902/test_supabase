import React, { useState } from 'react'
import { supabase } from './supabaseClient'

export default function SignUp({ onLogin }) {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [message, setMessage] = useState('')

    const handleSignUp = async (e) => {
        e.preventDefault()
        const { data, error } = await supabase.auth.signUp({ email, password })
        if (error) {
            setMessage(`❌ ${error.message}`)
        } else {
            setMessage('✅ 회원가입 성공! 이메일 인증을 확인하세요.')
            // 자동 로그인 상태 업데이트
            if (onLogin) {
                onLogin()
            }
        }
    }

    return (
        <div style={{ marginTop: 50 }}>
            <h2>회원가입</h2>
            <form onSubmit={handleSignUp}>
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
                <button type="submit">회원가입</button>
            </form>
            <p>{message}</p>
        </div>
    )
}