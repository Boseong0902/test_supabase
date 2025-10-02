package com.example.supabase.repository;

import com.example.supabase.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface roomRepository extends JpaRepository<Room, Long> {
}