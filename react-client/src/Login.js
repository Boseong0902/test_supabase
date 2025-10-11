// import React, { useState } from 'react'
// import { supabase } from './supabaseClient'
//
// export default function Login() {
//     const [email, setEmail] = useState('')
//     const [password, setPassword] = useState('')
//     const [token, setToken] = useState('')
//     const [message, setMessage] = useState('')
//
//     const handleLogin = async (e) => {
//         e.preventDefault()
//         const { data, error } = await supabase.auth.signInWithPassword({ email, password })
//         if (error) setMessage(`❌ ${error.message}`)
//         else {
//             setToken(data.session.access_token)
//             setMessage('✅ 로그인 성공!')
//             console.log('Access Token:', data.session.access_token)
//         }
//     }
//
//     return (
//         <div style={{ marginTop: 50 }}>
//             <h2>로그인</h2>
//             <form onSubmit={handleLogin}>
//                 <input
//                     type="email"
//                     placeholder="이메일"
//                     onChange={(e) => setEmail(e.target.value)}
//                 />
//                 <input
//                     type="password"
//                     placeholder="비밀번호"
//                     onChange={(e) => setPassword(e.target.value)}
//                 />
//                 <button type="submit">로그인</button>
//             </form>
//             <p>{message}</p>
//             {token && <pre>{token}</pre>}
//         </div>
//     )
// }

//############################################

import React, { useState } from 'react'
import axios from 'axios'
import { supabase } from './supabaseClient'

export default function Login() {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [token, setToken] = useState('')
    const [message, setMessage] = useState('')

    const handleLogin = async (e) => {
        e.preventDefault()
        const { data, error } = await supabase.auth.signInWithPassword({ email, password })

        if (error) {
            setMessage(`❌ ${error.message}`)
            return
        }

        // access_token 가져오기
        const accessToken = data.session.access_token
        setToken(accessToken)
        setMessage('✅ 로그인 성공! 서버에 사용자 정보 동기화 중...')

        try {
            // Spring 서버로 access_token 전달
            await axios.post(
                'http://localhost:8080/users/sync',
                {}, // body는 비워둠
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                }
            )
            setMessage('✅ 로그인 성공 및 서버 동기화 완료!')
        } catch (err) {
            console.error(err)
            setMessage('⚠️ 로그인은 성공했지만 서버 동기화 실패!')
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

            {/* 토큰 확인용 */}
            {token && (
                <details>
                    <summary>Access Token 보기</summary>
                    <pre style={{ textAlign: 'left', whiteSpace: 'pre-wrap' }}>
                        {token}
                    </pre>
                </details>
            )}
        </div>
    )
}