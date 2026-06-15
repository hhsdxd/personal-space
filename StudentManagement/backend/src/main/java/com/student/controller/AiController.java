package com.student.controller;

import com.student.ai.AiService;
import com.student.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public ApiResponse<?> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ApiResponse.error(400, "消息不能为空");
        }
        String reply = aiService.chat(message.trim());
        Map<String, String> result = new HashMap<>();
        result.put("reply", reply);
        return ApiResponse.success(result);
    }
}
