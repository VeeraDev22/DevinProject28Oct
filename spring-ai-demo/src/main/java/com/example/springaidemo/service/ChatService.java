package com.example.springaidemo.service;

import com.example.springaidemo.config.AiModelsProperties;
import org.springframework.ai.bedrock.converse.BedrockChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final AiModelsProperties models;

    public ChatService(ChatClient.Builder chatClientBuilder, AiModelsProperties models) {
        this.chatClient = chatClientBuilder.build();
        this.models = models;
    }

    /** Simple blocking chat call. modelAlias may be null -> uses default model. */
    public String chat(String message, String modelAlias) {
        return chatClient.prompt()
                .user(message)
                .options(options(modelAlias))
                .call()
                .content();
    }

    /** Chat with a system prompt + user message. */
    public String chat(String systemPrompt, String message, String modelAlias) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .options(options(modelAlias))
                .call()
                .content();
    }

    /** Streaming chat - tokens are emitted as they arrive from Bedrock. */
    public Flux<String> stream(String message, String modelAlias) {
        return chatClient.prompt()
                .user(message)
                .options(options(modelAlias))
                .stream()
                .content();
    }

    /** Build per-request options; overriding the model id picked by alias. */
    private BedrockChatOptions options(String modelAlias) {
        if (modelAlias == null || modelAlias.isBlank()) {
            return BedrockChatOptions.builder().build();
        }
        return BedrockChatOptions.builder()
                .model(models.resolve(modelAlias))
                .build();
    }
}
