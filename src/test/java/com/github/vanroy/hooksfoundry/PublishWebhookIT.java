package com.github.vanroy.hooksfoundry;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;
import java.util.concurrent.TimeUnit;

class PublishWebhookIT extends AbstractIntegrationTest {

    @Test
    @Sql({"data/truncate_db.sql", "data/one_subscription.sql"})
    void shouldPublishEventToSubscription() {

        wireMock.register(WireMock.post("/test")
                .willReturn(WireMock.ok()));

        publishKafkaMessage("orders", "key", new Event("tenant-01", 1234L, "Plasta"),
                Map.of(
                        "ce_specversion", "1.0",
                        "ce_id", "1234-1234-1234",
                        "ce_source", "order-service",
                        "ce_type", "order.created",
                        "content-type", "application/json"
                ));

        Awaitility.await()
                .pollInterval(1, TimeUnit.SECONDS)
                .atMost(60, TimeUnit.SECONDS)
                .untilAsserted(() -> wireMock.verifyThat(WireMock
                        .postRequestedFor(WireMock.urlEqualTo("/test"))
                        .withHeader("Content-Type", WireMock.equalTo("application/json"))
                        .withHeader("X-HookFoundry-Event", WireMock.equalTo("order.created"))
                        .withRequestBody(WireMock.equalToJson(
                                """
                                            {
                                              "order_id": 1234,
                                              "completed": true,
                                              "product_name": "Plasta"
                                            }
                                        """
                        ))
                ));
    }

    public record Event(String clientId, Long orderId, String productName) {
    }
}
