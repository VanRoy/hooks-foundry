package com.github.vanroy.hooksfoundry.domain.models;

import lombok.extern.slf4j.Slf4j;
import software.amazon.event.ruler.GenericMachine;

import java.io.IOException;
import java.util.Collection;

@Slf4j
public class RuleMatcher {

    GenericMachine<Rule> eventMachine = GenericMachine.<Rule>builder().build();

    public RuleMatcher(Collection<Rule> rules) {
        rules.forEach(this::addRule);
    }

    private void addRule(Rule rule) {
        try {
            this.eventMachine.addRule(rule, rule.getPattern());
        } catch (IOException e) {
            log.error("Invalid rule pattern : {}", rule.getPattern());
        }
    }

    public Collection<MatchedRule> evaluate(InputEvent event) {
        return this.eventMachine
                .rulesForJSONEvent(event.asJsonNode())
                .stream().map(r -> this.toMatchedRule(r, event))
                .toList();
    }

    private MatchedRule toMatchedRule(Rule rule, InputEvent event) {
        return MatchedRule.builder()
                .tenantId(rule.tenantIdOf(event))
                .eventType(rule.eventTypeOf(event))
                .inputEvent(event)
                .rule(rule)
                .build();
    }
}
