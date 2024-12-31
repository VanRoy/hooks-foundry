package com.github.vanroy.hooksfoundry.adapters.httpsender;

import com.github.vanroy.hooksfoundry.domain.models.Event;
import com.github.vanroy.hooksfoundry.domain.ports.EventSender;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class HttpClientEventSender implements EventSender {

    private final RestClient restClient;
    private String headerPrefix = "X-HookFoundry";

    public HttpClientEventSender(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public void send(Event event) {

        var resp = restClient.post()
                .uri(event.getDestination())
                .contentType(MediaType.APPLICATION_JSON)
                .header(this.headerPrefix + "-Event", event.getType())
                .header(this.headerPrefix + "-Id", event.getId().toString())
                .header(this.headerPrefix + "-Subscription-Id", event.getSubscriptionId().toString())
                .header(this.headerPrefix + "-Signature", event.getSignature())
                .body(event.getPayload())
                .retrieve().toBodilessEntity();
    }
}
