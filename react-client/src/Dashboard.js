import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { supabase } from './supabaseClient'

export default function Dashboard() {
    const [users, setUsers] = useState([])

    useEffect(() => {
        const fetchUsers = async () => {
            const { data: { session } } = await supabase.auth.getSession()
            if (!session) return console.error('로그인 필요')
            const token = session.access_token

            const res = await axios.get('http://localhost:8080/users', {
                headers: { Authorization: `Bearer ${token}` }
            })
            setUsers(res.data)
        }
        fetchUsers()
    }, [])

    return (
        <div>
            <h2>내 사용자 데이터</h2>
            <ul>
                {users.map((u) => (
                    <li key={u.id}>{u.name}</li>
                ))}
            </ul>
        </div>
    )
}