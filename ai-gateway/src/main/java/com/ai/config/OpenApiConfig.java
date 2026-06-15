package com.ai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI aiGatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI 智能网关 API")
                        .description("通义千问 SSE 流式对话 · 会话记忆 · 上下文感知")
                        .version("1.0.0"));
    }
}
