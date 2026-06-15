package com.ai.controller;

import com.ai.service.AiChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * AI 对话 REST API
 */
@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /**
     * SSE 流式对话
     * 请求体: {"message":"你好","sessionId":"uuid","context":"general"}
     * 响应: text/event-stream — 逐字推送 {"content":"字"}
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "");
        String sessionId = request.getOrDefault("sessionId", "default");
        String context = request.getOrDefault("context", "general");

        if (message.trim().isEmpty()) {
            return Flux.just("data: {\"error\":\"消息不能为空\"}\n\n");
        }

        return aiChatService.chatStream(sessionId, message.trim(), context)
                .map(chunk -> "data: " + chunk + "\n\n")
                .startWith("data: {\"start\":true}\n\n")
                .concatWithValues("data: [DONE]\n\n");
    }

    /**
     * 清除会话
     */
    @DeleteMapping("/session/{sessionId}")
    public Map<String, Object> clearSession(@PathVariable String sessionId) {
        aiChatService.clearSession(sessionId);
        return Map.of("success", true, "message", "会话已清除");
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "ok", "contexts", aiChatService.getContexts());
    }
}
