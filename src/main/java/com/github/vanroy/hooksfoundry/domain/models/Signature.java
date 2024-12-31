package com.github.vanroy.hooksfoundry.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Signature {

    private static final Duration EXPIRATION_TIME = Duration.ofDays(1);
    private final UUID secret;
    private Instant expiration;

    public Signature() {
        this.secret = UUID.randomUUID();
        this.expiration = null;
    }

    public void expire() {
        this.expiration = Instant.now().plus(EXPIRATION_TIME).truncatedTo(ChronoUnit.SECONDS);
    }
}
