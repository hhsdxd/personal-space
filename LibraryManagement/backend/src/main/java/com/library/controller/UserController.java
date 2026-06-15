package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.success(userService.listAll().stream().map(u -> {
            java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("role", u.getRole());
            map.put("createdAt", u.getCreatedAt());
            return map;
        }).collect(java.util.stream.Collectors.toList()));
    }
}
