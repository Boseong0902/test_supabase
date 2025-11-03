package com.example.supabase.controller;

import com.example.supabase.domain.User;
import com.example.supabase.repository.userRepository;
// import com.example.supabase.service.SupabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class userController {

    private final userRepository userRepository;
    // private final SupabaseService supabaseService;

    public userController(userRepository userRepository) {
        // public userController(userRepository userRepository, SupabaseService supabaseService) {
        this.userRepository = userRepository;
        // this.supabaseService = supabaseService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> body) {
        // 기존 토큰 인증방식 바꿈 -> JWTFilter에서 이미 인증 처리 완료 - SecurityCContext에서 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "인증이 필요합니다."));
        }

        String uuid = (String) authentication.getPrincipal();
        
        if (uuid == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "사용자 정보를 찾을 수 없습니다."));
        }

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

        /* 기존 방식 -> 수동으로 토큰 검증 -> 현재는 SecurityConfig에 등록된 JWTFilter로 검증
        public ResponseEntity<?> updateUser(
                @RequestHeader(value = "Authorization", required = false) String authorization,
                @RequestBody Map<String, Object> body
        ) {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "로그인 토큰이 없습니다."));
            }

            try {
                String accessToken = authorization.replace("Bearer ", "").trim();
                Map<String, Object> userInfo = supabaseService.getUserInfo(accessToken);

                if (userInfo == null || userInfo.get("id") == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "유효하지 않은 토큰입니다."));
                }

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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", e.getMessage()));
            }
        }
        */
    }
}