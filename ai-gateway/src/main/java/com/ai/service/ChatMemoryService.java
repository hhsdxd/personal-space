package com.ai.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话记忆服务 — 每个session保留最近N轮对话
 */
@Service
public class ChatMemoryService {

    private static final int MAX_HISTORY = 20;
    private static final int MAX_SESSIONS = 500;

    private final ConcurrentHashMap<String, LinkedList<Map<String, String>>> store = new ConcurrentHashMap<>();

    public List<Map<String, String>> getHistory(String sessionId) {
        LinkedList<Map<String, String>> h = store.get(sessionId);
        return h != null ? new ArrayList<>(h) : new ArrayList<>();
    }

    public void append(String sessionId, String role, String content) {
        LinkedList<Map<String, String>> h = store.computeIfAbsent(sessionId, k -> new LinkedList<>());
        Map<String, String> msg = new HashMap<>();
        msg.put("role", role);
        msg.put("content", content);
        h.add(msg);
        while (h.size() > MAX_HISTORY) {
            h.removeFirst();
        }
        if (store.size() > MAX_SESSIONS) {
            String oldest = store.keys().nextElement();
            store.remove(oldest);
        }
    }

    public void clear(String sessionId) {
        store.remove(sessionId);
    }

    public int sessionCount() {
        return store.size();
    }
}
