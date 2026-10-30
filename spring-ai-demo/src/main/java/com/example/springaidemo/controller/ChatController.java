package com.example.springaidemo.controller;

import com.example.springaidemo.config.AiModelsProperties;
import com.example.springaidemo.service.ChatService;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;
    private final AiModelsProperties models;

    public ChatController(ChatService chatService, AiModelsProperties models) {
        this.chatService = chatService;
        this.models = models;
    }

    /** List the model aliases configured in application.yml. */
    @GetMapping("/models")
    public Map<String, String> models() {
        return models.getModels();
    }

    /**
     * GET /api/chat?message=Hello&model=haiku
     * model is optional - defaults to the model in application.yml (haiku).
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String message,
                       @RequestParam(required = false) String model) {
        return chatService.chat(message, model);
    }

    /**
     * POST /api/chat  {"message": "Hi", "model": "sonnet", "system": "You are a pirate"}
     * All fields except "message" are optional.
     */
    @PostMapping("/chat")
    public String chatPost(@RequestBody ChatRequest request) {
        return chatService.chat(request.system(), request.message(), request.model());
    }

    /** GET /api/chat/stream?message=... - streams the reply token by token. */
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String message,
                               @RequestParam(required = false) String model) {
        return chatService.stream(message, model);
    }

    public record ChatRequest(String message, String model, String system) {}
}
