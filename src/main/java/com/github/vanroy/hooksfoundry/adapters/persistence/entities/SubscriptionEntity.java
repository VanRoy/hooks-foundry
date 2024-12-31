package com.github.vanroy.hooksfoundry.adapters.persistence.entities;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.postgresql.util.PGobject;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@Table("subscription")
@EqualsAndHashCode(of = {"id"})
public class SubscriptionEntity {

    @Id
    private final UUID id;
    private final String tenantId;
    private final String name;
    private final String url;
    private final String status;
    private final Set<String> eventTypes;
    private final PGobject signatures;
    @ReadOnlyProperty
    private final Instant createdAt;
    @ReadOnlyProperty
    private final Instant updatedAt;
}
