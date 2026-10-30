# spring-ai-demo

A learning project showing how to connect a Spring Boot app to **Anthropic Claude
models on AWS Bedrock** using **Spring AI** (Bedrock Converse API).

- Spring Boot `3.5.16`
- Spring AI `1.1.8`
- Java `21`

## Models used

Configured under `app.ai.models` in `src/main/resources/application.yml`:

| Alias  | Bedrock model / inference profile ID                    |
|--------|---------------------------------------------------------|
| haiku  | `global.anthropic.claude-haiku-4-5-20251001-v1:0`        |
| sonnet | `global.anthropic.claude-sonnet-4-6`                     |
| opus   | `global.anthropic.claude-opus-4-5-20251101-v1:0`         |

> Note: `global.*` IDs are Bedrock **cross-region inference profiles**. If a call
> fails with "model not found", copy the exact ID from AWS Console → Bedrock →
> Model access (some IDs end with a date + `-v1:0` suffix).

## 1. Add your credentials

Bedrock authenticates with **AWS credentials**, not a single API key.
Either edit `application.yml`:

```yaml
spring:
  ai:
    bedrock:
      aws:
        region: us-east-1
        access-key: YOUR_AWS_ACCESS_KEY_HERE
        secret-key: YOUR_AWS_SECRET_KEY_HERE
```

or export environment variables (recommended — keeps secrets out of the file):

```bash
export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
export AWS_SESSION_TOKEN=...   # only if using temporary credentials
export AWS_REGION=us-east-1
```

You also need **model access enabled** in AWS Console → Bedrock → Model access
for the Anthropic models, in a region that supports them.

## 2. Run

```bash
cd spring-ai-demo
mvn spring-boot:run
```

## 3. Try it

```bash
# List configured models
curl http://localhost:8080/api/models

# Chat with the default model (haiku)
curl "http://localhost:8080/api/chat?message=Explain+Spring+AI+in+one+sentence"

# Pick a model by alias
curl "http://localhost:8080/api/chat?message=Hello&model=opus"
curl "http://localhost:8080/api/chat?message=Hello&model=sonnet"

# POST with a system prompt
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Tell me a joke", "model": "haiku", "system": "You are a pirate"}'

# Streaming response (server-sent events)
curl -N "http://localhost:8080/api/chat/stream?message=Count+to+10&model=haiku"
```

## How it works

- `spring-ai-starter-model-bedrock-converse` auto-configures a `ChatClient`
  backed by Bedrock's Converse API using the `spring.ai.bedrock.*` properties.
- `ChatController` exposes REST endpoints; `ChatService` calls `ChatClient`.
- Per-request model selection is done by passing `BedrockChatOptions` with a
  different `model` ID — one `ChatClient` serves all three models.
- `AiModelsProperties` maps friendly aliases (`haiku`/`sonnet`/`opus`) to the
  full model IDs.

## Project layout

```
src/main/java/com/example/springaidemo/
├── SpringAiDemoApplication.java
├── config/AiModelsProperties.java     # binds app.ai.models aliases
├── service/ChatService.java           # ChatClient calls (sync + streaming)
└── controller/ChatController.java     # /api/chat, /api/chat/stream, /api/models
src/main/resources/application.yml     # credentials + model config
```

## Next learning steps

- Prompt templates & `PromptTemplate`
- Chat memory / conversation history (`ChatMemory`, advisors)
- Structured output (`entity()` to map replies to Java records)
- RAG with embeddings + a vector store
- Tool/function calling
