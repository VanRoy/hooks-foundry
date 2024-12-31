package com.github.vanroy.hooksfoundry.endpoints.apis.dtos;

import com.github.vanroy.hooksfoundry.domain.usecases.CreateSubscriptionUseCase;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public record CreateSubscriptionRequestBody(
        @NotNull @URL String url,
        String name,
        Set<String> eventTypes
) {
    public CreateSubscriptionUseCase.Request toRequest(String tenantId) {
        return CreateSubscriptionUseCase.Request.builder()
                .tenantId(tenantId)
                .url(this.url)
                .name(this.name)
                .eventTypes(Optional.ofNullable(this.eventTypes).orElseGet(Collections::emptySet))
                .build();
    }
}
