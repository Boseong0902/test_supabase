import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { supabase } from './supabaseClient'

export default function Dashboard() {
    const [users, setUsers] = useState([])
    const [name, setName] = useState('')
    const [roomId, setRoomId] = useState('')
    const [message, setMessage] = useState('')

    const handleUpdate = async () => {
        const { data: { session } } = await supabase.auth.getSession()
        if (!session) {
            setMessage('로그인이 필요합니다.')
            return
        }

        const token = session.access_token

        try {
            const res = await axios.post(
                'http://localhost:8080/users/update',
                { name, room_id: roomId },
                { headers: { Authorization: `Bearer ${token}` } }
            )
            setMessage('✅ 사용자 정보 업데이트 성공!')
            console.log('업데이트 결과:', res.data)
        } catch (err) {
            console.error(err)
            setMessage('❌ 업데이트 실패')
        }
    }

    const fetchUsers = async () => {
        const res = await axios.get('http://localhost:8080/users')
        setUsers(res.data)
    }

    useEffect(() => {
        fetchUsers()
    }, [])

    return (
        <div>
            <h2>내 사용자 데이터</h2>
            <button onClick={fetchUsers}>새로고침</button>
            <ul>
                {users.map((u) => (
                    <li key={u.id}>
                        {u.name || '(이름 없음)'} — Room: {u.room_id || '-'}
                    </li>
                ))}
            </ul>

            <hr />
            <h3>이름 / Room ID 입력</h3>
            <input
                type="text"
                placeholder="이름 입력"
                value={name}
                onChange={(e) => setName(e.target.value)}
            />
            <input
                type="number"
                placeholder="Room ID 입력"
                value={roomId}
                onChange={(e) => setRoomId(e.target.value)}
            />
            <button onClick={handleUpdate}>업데이트</button>
            <p>{message}</p>
        </div>
    )
}