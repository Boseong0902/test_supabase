package com.example.supabase.controller;

import com.example.supabase.domain.User;
import com.example.supabase.repository.userRepository;
import com.example.supabase.service.SupabaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class userController {

    private final userRepository userRepository;
    private final SupabaseService supabaseService;

    public userController(userRepository userRepository, SupabaseService supabaseService) {
        this.userRepository = userRepository;
        this.supabaseService = supabaseService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> body
    ) {
        // ✅ 1️⃣ Authorization 헤더가 없으면 즉시 거절
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인 토큰이 없습니다."));
        }

        try {
            String accessToken = authorization.replace("Bearer ", "").trim();
            Map<String, Object> userInfo = supabaseService.getUserInfo(accessToken);

            // ✅ 2️⃣ 토큰이 유효하지 않으면 SupabaseService에서 예외 발생
            if (userInfo == null || userInfo.get("id") == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "유효하지 않은 토큰입니다."));
            }

            // ✅ 3️⃣ 유저 정보 업데이트
            String uuid = (String) userInfo.get("id");
            String name = (String) body.get("name");
            Long roomId = Long.valueOf(body.get("room_id").toString());

            User user = userRepository.findAll()
                    .stream()
                    .filter(u -> uuid.equals(u.getUuid()))
                    .findFirst()
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setUuid(uuid);
                        return newUser;
                    });

            user.setName(name);
            user.setRoom_id(roomId);

            User saved = userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "✅ 사용자 업데이트 성공",
                    "uuid", saved.getUuid(),
                    "name", saved.getName(),
                    "room_id", saved.getRoom_id()
            ));

        } catch (RuntimeException e) {
            // ✅ 4️⃣ Supabase 인증 실패 시 예외 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}