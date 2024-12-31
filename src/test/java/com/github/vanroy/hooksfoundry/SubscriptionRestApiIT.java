package com.github.vanroy.hooksfoundry;

import io.restassured.http.ContentType;
import org.assertj.db.type.DateTimeValue;
import org.assertj.db.type.TimeValue;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@Sql("data/truncate_db.sql")
class SubscriptionRestApiIT extends AbstractIntegrationTest {

    @Test
    void shouldCreateSubscription() {
        givenRestApi()
                .contentType(ContentType.JSON)
                .pathParam("tenantId", "1")
                .body("""
                        {
                         "url": "https://localhost:1234",
                         "name": "My first webhook",
                         "event_types": ["order.created", "order.publish"]
                         }""")
                .when()
                .post("/tenants/{tenantId}/subscriptions")
                .then()
                .log().all()
                .statusCode(201);

        assertThatTable("subscription").hasNumberOfRows(1)
                .row()
                .value("id").isUUID()
                .value("tenant_id").isEqualTo("1")
                .value("name").isEqualTo("My first webhook")
                .value("url").isEqualTo("https://localhost:1234")
                .value("status").isEqualTo("enabled")
                .value("event_types").is(pgArrayContainsExactly("order.created", "order.publish"))
                .value("signatures").is(pgObjectMatch("\\[\\{\"secret\": \"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\", \"expiration\": null}]"))
                .value("created_at").isCloseTo(DateTimeValue.now(), TimeValue.of(0, 1))
                .value("updated_at").isCloseTo(DateTimeValue.now(), TimeValue.of(0, 1))
        ;
    }
}
