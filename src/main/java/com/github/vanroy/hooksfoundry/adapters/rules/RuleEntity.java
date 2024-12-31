package com.github.vanroy.hooksfoundry.adapters.rules;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RuleEntity {

    private String pattern;

    private String eventType;
    private String eventTypePath;

    private String tenantIdPath;

    private Transformer transformer;

    @Data
    public static class Transformer {
        private HashMap<String, String> variables;
        private String template;
    }
}
