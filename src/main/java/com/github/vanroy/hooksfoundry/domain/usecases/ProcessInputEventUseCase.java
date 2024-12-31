package com.github.vanroy.hooksfoundry.domain.usecases;

import com.github.vanroy.hooksfoundry.domain.models.*;
import com.github.vanroy.hooksfoundry.domain.ports.EventSender;
import com.github.vanroy.hooksfoundry.domain.ports.RuleRepository;
import com.github.vanroy.hooksfoundry.domain.ports.SubscriptionRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ProcessInputEventUseCase {

    private final EventSender sender;
    private final SubscriptionRepository subscriptionRepository;
    private final RuleMatcher ruleMatcher;

    public ProcessInputEventUseCase(EventSender sender, RuleRepository ruleRepository, SubscriptionRepository subscriptionRepository) {
        this.sender = sender;
        this.ruleMatcher = new RuleMatcher(ruleRepository.findAll());
        this.subscriptionRepository = subscriptionRepository;
    }

    @SneakyThrows
    public void execute(InputEvent inputEvent) {
        var matchedRules = this.ruleMatcher.evaluate(inputEvent);
        log.info("Matched {} rules", matchedRules.size());
        log.atDebug().addArgument(inputEvent::asString).log("Not matched event : {}");

        matchedRules.forEach(this::processRule);
    }

    void processRule(MatchedRule rule) {
        this.subscriptionRepository
                .findEnabledByTenantAndEventType(rule.getTenantId(), rule.getEventType())
                .forEach(s -> this.processSubscription(s, rule));
    }

    void processSubscription(Subscription subscription, MatchedRule rule) {
        var payload = rule.transformPayload();
        var eventToSend = Event.builder()
                .id(UUID.randomUUID()) // TODO(jroy) : Keep static for each retry
                .subscriptionId(subscription.getId())
                .type(rule.getEventType())
                .payload(payload)
                .signature(subscription.sign(payload))
                .destination(subscription.getUrl())
                .build();
        this.sender.send(eventToSend);
    }
}
