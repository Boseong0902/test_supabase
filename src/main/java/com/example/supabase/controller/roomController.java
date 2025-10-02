package com.example.supabase.controller;

import com.example.supabase.domain.Room;
import com.example.supabase.domain.User;
import com.example.supabase.repository.roomRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class roomController {
    private final com.example.supabase.repository.roomRepository roomRepository;

    public roomController(roomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @PostMapping
    public Room saveRoom(@RequestBody Room room) {
        return roomRepository.save(room);
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}