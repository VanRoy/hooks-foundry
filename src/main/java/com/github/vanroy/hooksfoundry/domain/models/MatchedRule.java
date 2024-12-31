package com.github.vanroy.hooksfoundry.domain.models;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
public class MatchedRule {
    @Getter
    private final String eventType;
    @Getter
    private final String tenantId;
    private final Rule rule;
    private final InputEvent inputEvent;

    public String transformPayload() {
        return this.rule.transformPayload(
                Map.of(
                        "hf.event.type", eventType,
                        "hf.event", inputEvent
                ),
                inputEvent
        );
    }
}
