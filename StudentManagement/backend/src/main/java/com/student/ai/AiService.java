package com.student.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AiService {

    private final String apiKey;
    private final String model;
    private final StudentRepository studentRepository;

    public AiService(@Value("${ai.dashscope.api-key:}") String apiKey,
                     @Value("${ai.dashscope.model:qwen-plus}") String model,
                     StudentRepository studentRepository) {
        this.apiKey = apiKey;
        this.model = model;
        this.studentRepository = studentRepository;
    }

    public String chat(String userMessage) {
        try {
            String systemPrompt = buildSystemPrompt();
            String response = callDashScope(systemPrompt, userMessage);
            return response;
        } catch (Exception e) {
            return "AI 服务暂时不可用：" + e.getMessage();
        }
    }

    private String buildSystemPrompt() {
        long total = studentRepository.count();
        long male = studentRepository.countByGender("男");
        long female = studentRepository.countByGender("女");

        return "你是学生管理系统的 AI 助手。你可以帮用户查询、分析、管理学生数据。回答要简洁专业。\n"
                + "当前系统数据：学生总数 " + total + " 人，男生 " + male + " 人，女生 " + female + " 人。\n"
                + "你可以帮用户：统计分析、查找学生、给出管理建议、解答系统使用问题。";
    }

    private String callDashScope(String systemPrompt, String userMessage) throws Exception {
        URL url = new URL("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        body.put("messages", messages);
        body.put("temperature", 0.7);
        body.put("max_tokens", 1000);

        String json = mapper.writeValueAsString(body);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        if (code == 200) {
            Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8").useDelimiter("\\A");
            String result = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
            return extractContent(result, mapper);
        } else {
            Scanner scanner = new Scanner(conn.getErrorStream(), "UTF-8").useDelimiter("\\A");
            String err = scanner.hasNext() ? scanner.next() : "HTTP " + code;
            scanner.close();
            return "API 调用失败：" + err;
        }
    }

    private String extractContent(String json, ObjectMapper mapper) throws Exception {
        Map<String, Object> map = mapper.readValue(json, Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) map.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
            if (msg != null && msg.get("content") != null) {
                return msg.get("content").toString();
            }
        }
        return "AI 返回为空";
    }
}
