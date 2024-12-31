package com.github.vanroy.hooksfoundry.adapters.persistence;

import com.github.vanroy.hooksfoundry.adapters.persistence.converters.SignaturesConverter;
import com.github.vanroy.hooksfoundry.adapters.persistence.entities.SubscriptionEntity;
import com.github.vanroy.hooksfoundry.domain.models.Subscription;
import com.github.vanroy.hooksfoundry.domain.models.SubscriptionStatus;
import com.github.vanroy.hooksfoundry.domain.ports.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class SubscriptionJdbcRepository implements SubscriptionRepository {

    private final JdbcAggregateTemplate aggregateTemplate;
    private final JdbcRepository repository;
    private final SignaturesConverter signaturesConverter;

    @Override
    public Subscription create(Subscription subscription) {
        var entity = this.aggregateTemplate.insert(toEntity(subscription));
        return toModel(entity);
    }

    @Override
    public Collection<Subscription> findEnabledByTenantAndEventType(String tenantId, String eventType) {
        return this.repository.findEnabledByTenantAndEventType(tenantId, eventType)
                .map(this::toModel)
                .toList();
    }

    private interface JdbcRepository extends CrudRepository<SubscriptionEntity, UUID> {
        @Query(
                """
                        select
                         id, tenant_id, name, url, status, event_types, signatures, created_at, updated_at from subscription 
                        where
                         tenant_id = :tenantId and
                         :eventType = ANY(event_types)
                        """
        )
        Stream<SubscriptionEntity> findEnabledByTenantAndEventType(String tenantId, String eventType);
    }

    @SneakyThrows
    private Subscription toModel(SubscriptionEntity entity) {
        return Subscription.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .name(entity.getName())
                .url(entity.getUrl())
                .status(SubscriptionStatus.of(entity.getStatus()))
                .eventTypes(entity.getEventTypes())
                .signatures(signaturesConverter.from(entity.getSignatures()))
                .build();
    }

    private SubscriptionEntity toEntity(Subscription model) {
        return SubscriptionEntity.builder()
                .id(model.getId())
                .tenantId(model.getTenantId())
                .name(model.getName())
                .url(model.getUrl())
                .status(model.getStatus() == null ? null : model.getStatus().name().toLowerCase())
                .eventTypes(model.getEventTypes())
                .signatures(signaturesConverter.to(model.getSignatures()))
                .build();
    }
}
