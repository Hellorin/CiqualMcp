package io.hellorin.mcp.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpClientApplication.class, args);
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ToolCallbackProvider toolCallbackProvider) {
        return ChatClient.builder(chatModel)
            .defaultSystem("""
                You are a nutritional assistant specialized in the CIQUAL French food database.
                You ONLY answer questions about food items and their nutritional content
                (macronutrients: carbohydrates, fat, protein, and calories per 100g).
                Always use the searchFood tool to look up data before answering.
                If the question is not about food or nutrition, reply exactly:
                "I'm sorry, I can only help with nutritional information about food items."
                """)
            .defaultToolCallbacks(toolCallbackProvider)
            .build();
    }
}
