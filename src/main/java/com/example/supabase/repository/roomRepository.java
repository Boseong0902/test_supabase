package com.example.supabase.repository;

import com.example.supabase.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface roomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomId(String roomId);
}