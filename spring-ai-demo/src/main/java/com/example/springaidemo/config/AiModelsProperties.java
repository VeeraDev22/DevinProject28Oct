package com.example.springaidemo.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the friendly model aliases defined under "app.ai.models" in
 * application.yml, e.g. haiku -> global.anthropic.claude-haiku-4-5-...
 */
@ConfigurationProperties(prefix = "app.ai")
public class AiModelsProperties {

    /** alias -> Bedrock model / inference-profile ID */
    private Map<String, String> models;

    public Map<String, String> getModels() {
        return models;
    }

    public void setModels(Map<String, String> models) {
        this.models = models;
    }

    /**
     * Resolve a user-supplied value to a full model ID. Accepts either an
     * alias ("haiku") or a raw model ID ("global.anthropic.claude-...").
     */
    public String resolve(String aliasOrId) {
        return models.getOrDefault(aliasOrId, aliasOrId);
    }
}
