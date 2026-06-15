package com.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * AI 对话服务 — SSE流式调用通义千问
 */
@Service
public class AiChatService {

    @Value("${ai.dashscope.api-key}")
    private String apiKey;

    @Value("${ai.dashscope.model:qwen-plus}")
    private String model;

    private final ChatMemoryService memoryService;
    private final ObjectMapper mapper = new ObjectMapper();
    private WebClient webClient;

    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    // 上下文System Prompt映射
    private static final Map<String, String> CONTEXT_PROMPTS = new LinkedHashMap<>();
    static {
        CONTEXT_PROMPTS.put("library", "你是图书管理系统的AI助手。你可以帮用户：\n"
                + "- 推荐图书、查询图书信息\n"
                + "- 解答借阅相关问题\n"
                + "- 分析借阅数据、给出管理建议\n"
                + "回答要简洁专业，用中文。");

        CONTEXT_PROMPTS.put("student", "你是学生管理系统的AI助手。你可以帮用户：\n"
                + "- 统计分析学生数据（性别比例、年龄分布、班级概况等）\n"
                + "- 查找学生信息\n"
                + "- 给出教学管理建议\n"
                + "- 解答系统使用问题\n"
                + "回答要简洁专业，用中文。");

        CONTEXT_PROMPTS.put("general", "你是全栈项目展示平台的AI助手。你可以：\n"
                + "- 介绍平台包含的模块（图书管理、学生管理）\n"
                + "- 解答技术栈相关问题（Spring Boot、Vue 3、MySQL、Docker等）\n"
                + "- 帮助访客了解项目功能和架构\n"
                + "回答要友好热情，用中文。");
    }

    public AiChatService(ChatMemoryService memoryService) {
        this.memoryService = memoryService;
    }

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(API_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * SSE 流式对话
     * @param sessionId 会话ID（前端生成UUID）
     * @param userMessage 用户消息
     * @param context 上下文类型：library / student / general
     * @return Flux<String> SSE事件流
     */
    public Flux<String> chatStream(String sessionId, String userMessage, String context) {
        // 1. 构建消息列表
        List<Map<String, String>> messages = buildMessages(sessionId, userMessage, context);

        // 2. 构建请求体
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("temperature", 0.7);
        body.put("max_tokens", 2000);
        body.put("stream", true);

        // 保存用户消息到记忆
        memoryService.append(sessionId, "user", userMessage);

        // 3. 发起SSE流式请求
        StringBuilder fullResponse = new StringBuilder();

        return webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(line -> !line.trim().isEmpty())
                .filter(line -> line.startsWith("data: "))
                .map(line -> line.substring(6)) // 去掉 "data: " 前缀
                .filter(data -> !"[DONE]".equals(data.trim()))
                .map(data -> {
                    try {
                        JsonNode node = mapper.readTree(data);
                        JsonNode choices = node.get("choices");
                        if (choices != null && choices.size() > 0) {
                            JsonNode delta = choices.get(0).get("delta");
                            if (delta != null) {
                                JsonNode content = delta.get("content");
                                if (content != null && !content.asText().isEmpty()) {
                                    String chunk = content.asText();
                                    fullResponse.append(chunk);
                                    // SSE格式: data: {"content":"文字片段"}
                                    Map<String, String> sse = new LinkedHashMap<>();
                                    sse.put("content", chunk);
                                    return mapper.writeValueAsString(sse);
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                    return ""; // 非内容帧返回空，下游过滤
                })
                .filter(s -> !s.isEmpty())
                .doOnComplete(() -> {
                    // 保存AI完整回复到记忆
                    if (fullResponse.length() > 0) {
                        memoryService.append(sessionId, "assistant", fullResponse.toString());
                    }
                })
                .doOnError(error -> {
                    // 错误时保存部分回复
                    if (fullResponse.length() > 0) {
                        memoryService.append(sessionId, "assistant", fullResponse.toString());
                    }
                });
    }

    /**
     * 构建消息列表：System Prompt + 历史消息 + 当前消息
     */
    private List<Map<String, String>> buildMessages(String sessionId, String userMessage, String context) {
        List<Map<String, String>> messages = new ArrayList<>();

        // System Prompt
        Map<String, String> sys = new LinkedHashMap<>();
        sys.put("role", "system");
        sys.put("content", CONTEXT_PROMPTS.getOrDefault(context, CONTEXT_PROMPTS.get("general")));
        messages.add(sys);

        // 历史消息
        messages.addAll(memoryService.getHistory(sessionId));

        // 当前用户消息
        Map<String, String> user = new LinkedHashMap<>();
        user.put("role", "user");
        user.put("content", userMessage);
        messages.add(user);

        return messages;
    }

    /**
     * 清除会话
     */
    public void clearSession(String sessionId) {
        memoryService.clear(sessionId);
    }

    /**
     * 获取所有可用的上下文类型
     */
    public Set<String> getContexts() {
        return CONTEXT_PROMPTS.keySet();
    }
}
