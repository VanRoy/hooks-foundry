package com.github.vanroy.hooksfoundry.domain.models;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class Event {
    UUID id;
    UUID subscriptionId;
    String type;
    String destination;
    String signature;
    String payload;
}
