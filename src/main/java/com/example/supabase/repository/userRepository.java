package com.example.supabase.repository;

import com.example.supabase.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userRepository extends JpaRepository<User, Long> {
    User findByUuid(String uuid);
}