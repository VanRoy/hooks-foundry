package com.github.vanroy.hooksfoundry.domain.ports;

import com.github.vanroy.hooksfoundry.domain.models.Subscription;

import java.util.Collection;

public interface SubscriptionRepository {

    Subscription create(Subscription subscription);

    Collection<Subscription> findEnabledByTenantAndEventType(String tenantId, String eventType);

//    findEnabledByTenantIdAndEventsType(String tenantId, EventType type);
}
