package com.example.supabase.controller;

import com.example.supabase.domain.User;
import com.example.supabase.repository.userRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class userController {
    private final com.example.supabase.repository.userRepository userRepository;

    public userController(userRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public User saveUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}