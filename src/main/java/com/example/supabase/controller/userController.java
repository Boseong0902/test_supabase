package com.example.supabase.controller;

import com.example.supabase.domain.User;
import com.example.supabase.repository.userRepository;
import com.example.supabase.service.SupabaseService;
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

    // ✅ React 로그인 후 토큰 동기화
    @PostMapping("/sync")
    public User syncUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();

        Map<String, Object> userInfo = supabaseService.getUserInfo(token);
        String uuid = (String) userInfo.get("id");
        String email = (String) userInfo.get("email");

        User existing = userRepository.findByUuid(uuid);
        if (existing != null) return existing;

        User newUser = new User();
        newUser.setUuid(uuid);
        newUser.setEmail(email);

        return userRepository.save(newUser);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}