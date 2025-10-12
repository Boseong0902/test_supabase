package com.example.supabase.controller;

import com.example.supabase.domain.Room;
import com.example.supabase.repository.roomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class roomController {
    private final roomRepository roomRepository;

    public roomController(roomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @PostMapping("/join")
    public ResponseEntity<Room> joinOrCreate(@RequestBody RoomRequest req) {
        return roomRepository.findByRoomId(req.roomId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Room newRoom = new Room();
                    newRoom.setRoomId(req.roomId());
                    newRoom.setMessage("새 방이 생성되었습니다!");
                    Room saved = roomRepository.save(newRoom);
                    return ResponseEntity.ok(saved);
                });
    }

    @PutMapping("/{roomId}/message")
    public ResponseEntity<Room> updateMessage(
            @PathVariable String roomId,
            @RequestBody MessageDto dto
    ) {
        return roomRepository.findByRoomId(roomId)
                .map(room -> {
                    room.setMessage(dto.message());
                    Room updated = roomRepository.save(room);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public record RoomRequest(String roomId) {}
    public record MessageDto(String message) {}
}