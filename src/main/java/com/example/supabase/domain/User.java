package com.example.supabase.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 내부 PK

    @Column(unique = true)
    private String uuid;  // Supabase user id

    private String email;
    private Long room_id;
    private String name;
}