// src/pages/Message.js
import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios'
import { supabase } from '../supabaseClient'

export default function Message() {
    const [roomId, setRoomId] = useState('')
    const [roomData, setRoomData] = useState(null)
    const [message, setMessage] = useState('')
    const [inputMessage, setInputMessage] = useState('')
    const [payload, setPayload] = useState(null)
    const channelRef = useRef(null)

    const joinRoom = async () => {
        if (!roomId) return alert('room_id를 입력하세요.')
        try {
            const res = await axios.post('http://localhost:8080/rooms/join', { roomId })
            const data = res.data
            setRoomData(data)
            setMessage(data.message ?? '')
            subscribeRoom(data.roomId)
        } catch (err) {
            console.error(err)
            alert('방 참가 실패')
        }
    }

    const subscribeRoom = async (rid) => {
        if (channelRef.current) {
            await channelRef.current.unsubscribe()
        }

        const channel = supabase
            .channel(`room-${rid}`)
            .on('postgres_changes', {
                event: '*',
                schema: 'public',
                table: 'rooms',
                filter: `room_id=eq.${rid}`,
            }, (payload) => {
                console.log('📡 이벤트 수신:', payload)
                setPayload(payload)
                if (payload.eventType === 'INSERT' || payload.eventType === 'UPDATE' || payload.eventType === 'DELETE') {
                    setMessage(payload.new?.message || '(삭제됨)')
                }
            })
            .subscribe((status) => {
                if (status === 'SUBSCRIBED') {
                    console.log(`✅ Realtime 연결됨 (room_id=${rid})`)
                }
            })

        channelRef.current = channel
    }

    const updateMessage = async () => {
        if (!roomData) return alert('방부터 참가하세요.')
        try {
            await axios.put(`http://localhost:8080/rooms/${roomData.roomId}/message`, {
                message: inputMessage
            })
            setInputMessage('')
        } catch (err) {
            console.error(err)
            alert('메시지 수정 실패')
        }
    }

    return (
        <div style={{ marginTop: 30 }}>
            <h2>Realtime 메시지</h2>

            <div>
                <input
                    placeholder="room_id 입력"
                    value={roomId}
                    onChange={(e) => setRoomId(e.target.value)}
                    style={{ width: 240 }}
                />
                <button onClick={joinRoom} style={{ marginLeft: 8 }}>참가 / 생성</button>
            </div>

            {roomData && (
                <div style={{ marginTop: 20 }}>
                    <h4>현재 메시지:</h4>
                    <pre>{message || '(메시지 없음)'}</pre>

                    <input
                        placeholder="새 메시지 입력"
                        value={inputMessage}
                        onChange={(e) => setInputMessage(e.target.value)}
                        style={{ width: 240 }}
                    />
                    <button onClick={updateMessage} style={{ marginLeft: 8 }}>수정</button>
                </div>
            )}

            <div style={{ marginTop: 20 }}>
                <h4>Realtime 이벤트 로그</h4>
                <pre style={{ background: '#f6f6f6', padding: 10 }}>
          {payload ? JSON.stringify(payload, null, 2) : '(대기 중)'}
        </pre>
            </div>
        </div>
    )
}