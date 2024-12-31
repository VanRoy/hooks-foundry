package com.github.vanroy.hooksfoundry.domain.ports;

import com.github.vanroy.hooksfoundry.domain.models.Event;

public interface EventSender {
    void send(Event event);
}
