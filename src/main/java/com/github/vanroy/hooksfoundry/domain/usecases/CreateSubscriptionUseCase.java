package com.github.vanroy.hooksfoundry.domain.usecases;

import com.github.vanroy.hooksfoundry.domain.models.Subscription;
import com.github.vanroy.hooksfoundry.domain.ports.SubscriptionRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreateSubscriptionUseCase {

    private final SubscriptionRepository repository;

    public Response execute(Request request) {
        // WebhookValidator.verifyNumberOfWebhooks(webhookRepository.countByAppId(creationRequest.getAppId()));
        // WebhookValidator.validateWebhook(creationRequest);
        var subscription = repository.create(request.toModel());

        return new Response();
    }

    @Value
    @Builder
    public static class Request {
        String tenantId;
        String name;
        String url;
        Set<String> eventTypes;

        Subscription toModel() {
            return new Subscription(tenantId, this.name, this.url, Optional.ofNullable(this.eventTypes).orElseGet(Collections::emptySet));
        }
    }

    @Builder
    public static class Response {
    }
/*
    private WebhookCreationResponse toCreationResponse(Webhook webhook) {
        return WebhookCreationResponse.builder()
            .id(webhook.getId())
            .url(webhook.getUrl())
            .name(webhook.getName())
            .signature(toResponseSignature(webhook.getSignatures().peek()))
            .status(webhook.getStatus())
            .eventTypes(webhook.getEventsType())
            .createdAt(webhook.getCreatedAt())
            .updatedAt(webhook.getUpdatedAt())
            .build();
    }

    private WebhookCreationResponse.Signature toResponseSignature(Signature signature) {
        return signature == null ? null : WebhookCreationResponse.Signature.builder()
            .expiration(signature.getExpiration())
            .secret(signature.getSecret())
            .build();
    }

 */
}
