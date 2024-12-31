package com.github.vanroy.hooksfoundry.domain.models;

public enum SubscriptionStatus {
    ENABLED,
    DISABLED,
    PAUSED;

    public static SubscriptionStatus of(String status) {
        return SubscriptionStatus.valueOf(status.toUpperCase());
    }
}
