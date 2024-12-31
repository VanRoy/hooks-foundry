package com.github.vanroy.hooksfoundry.domain.models;

import com.jayway.jsonpath.JsonPath;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import org.jspecify.annotations.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Value
public class Rule {

    UUID id;

    String pattern;

    @Nullable
    String eventType;
    @Nullable
    JsonPath eventTypePath;

    JsonPath tenantIdPath;

    @Nullable
    Transformer transformer;

    @Builder
    public Rule(UUID id, String pattern, @Nullable String eventType, @Nullable String eventTypePath, String tenantIdPath, @Nullable Transformer transformer) {
        this.id = id;
        this.pattern = pattern;
        this.eventType = eventType;
        this.eventTypePath = eventTypePath == null ? null : JsonPath.compile(eventTypePath);
        this.tenantIdPath = JsonPath.compile(tenantIdPath);
        this.transformer = transformer;
    }

    public String transformPayload(Map<String, Object> predefinedVariables, InputEvent inputEvent) {
        if (this.transformer == null) {
            return inputEvent.asString();
        }
        return this.transformer.transform(predefinedVariables, inputEvent);
    }

    public String tenantIdOf(InputEvent event) {
        return event.readPath(this.tenantIdPath, String.class);
    }

    public String eventTypeOf(InputEvent event) {
        if(StringUtils.hasText(this.eventType)) {
            return this.eventType;
        }
        return event.readPath(this.eventTypePath, String.class);
    }

    public static class Transformer {

        private static final Pattern VAR_EXP = Pattern.compile("(<[\\w_-]*>)", Pattern.MULTILINE);

        Map<String, JsonPath> variables;
        String template;
        Collection<String> templateVariables = new ArrayList<>();

        @Builder
        private Transformer(Map<String, String> variables, String template) {
            this.variables = compileVariables(variables);
            this.template = template;
            this.parseTemplate();
        }

        public String transform(Map<String, Object> predefinedVariables, InputEvent inputEvent) {
            var resolvedVariables = this.prepareVariables(inputEvent);
            resolvedVariables.putAll(predefinedVariables);
            return this.processTemplate(resolvedVariables);
        }

        private void parseTemplate() {
            var matcher = VAR_EXP.matcher(this.template);
            while (matcher.find()) {
                this.templateVariables.add(matcher.group(0));
            }
        }

        private Map<String, JsonPath> compileVariables(Map<String, String> variables) {
            if (variables == null) {
                return Collections.emptyMap();
            }
            return variables.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> JsonPath.compile(e.getValue())
            ));
        }

        @SneakyThrows
        private Map<String, Object> prepareVariables(InputEvent inputEvent) {
            if (this.variables == null || this.variables.isEmpty()) {
                return new HashMap<>();
            }

            return this.variables.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> inputEvent.readPath(e.getValue())
            ));
        }

        private String processTemplate(Map<String, Object> variables) {
            String result = this.template;

            for (String templateVariable : templateVariables) {
                result = result.replaceAll(templateVariable, variables.getOrDefault(templateVariable.substring(1, templateVariable.length() - 1), "null").toString());
            }

            return result;
        }
    }
}
