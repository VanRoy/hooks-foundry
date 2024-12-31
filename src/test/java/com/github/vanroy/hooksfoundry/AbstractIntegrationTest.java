package com.github.vanroy.hooksfoundry;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.api.Condition;
import org.assertj.db.api.ChangesAssert;
import org.assertj.db.api.TableAssert;
import org.assertj.db.output.Outputs;
import org.assertj.db.type.AssertDbConnection;
import org.assertj.db.type.AssertDbConnectionFactory;
import org.assertj.db.type.Changes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.postgresql.jdbc.PgArray;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.db.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DataSource dataSource;

    private AssertDbConnection assertDbConnection;
    private KafkaTemplate<String, Object> kafkaTemplate;
    protected WireMock wireMock;

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
    }

    @BeforeEach
    void setUpAssertDb() {
        this.assertDbConnection = AssertDbConnectionFactory.of(this.dataSource).create();
    }

    @BeforeEach
    void setupWireMock() {
        this.wireMock = new WireMock("localhost", 8888);
        this.wireMock.resetRequests();
        this.wireMock.resetMappings();
        this.wireMock.resetScenarios();
    }

    @BeforeEach
    void setUpKafkaTemplate() {
        //KafkaConnectionDetails
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.TYPE_MAPPINGS, "event:com.github.vanroy.hooksfoundry.PublishWebhookIT.Event");

        var producerFactory = new DefaultKafkaProducerFactory<String, Object>(props);

        this.kafkaTemplate = new KafkaTemplate<>(producerFactory);

        Map<String, Object> adminProp = new HashMap<>();
        adminProp.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        try (AdminClient client = AdminClient.create(adminProp)) {
            client.deleteTopics(List.of("orders"));
            client.createTopics(List.of(new NewTopic("orders", 1, (short) 1)));
        }
    }

    RequestSpecification givenRestApi() {
        return given();
    }

    void publishKafkaMessage(String topic, String key, Object data, Map<String, String> headers) {

        ProducerRecord<String, Object> pr = new ProducerRecord<>(
                topic, null, key, data, headers.entrySet().stream().<Header>map(
                e -> new RecordHeader(e.getKey(), e.getValue().getBytes())).toList()
        );
        this.kafkaTemplate.send(pr).join();
    }

    Changes assertDbChanges() {
        return this.assertDbConnection.changes().build().setStartPointNow();
    }

    ChangesAssert assertThatChanges(Changes dbChanges) {
        return assertThat(dbChanges.setEndPointNow());
    }

    TableAssert assertThatTable(String table) {
        return assertThat(this.assertDbConnection.table(table).build());
    }

    void outputTable(String table) {
        Outputs.output(this.assertDbConnection.table(table).build()).toConsole();
    }

    Condition<PGobject> pgObjectMatch(String expectedRegex) {
        return new Condition<>(new PgObjectMatch(expectedRegex), expectedRegex);
    }

    static class PgObjectMatch implements Predicate<PGobject> {
        private final Pattern pattern;

        public PgObjectMatch(String expectedRegex) {
            this.pattern = Pattern.compile(expectedRegex);
        }

        @Override
        public boolean test(PGobject pGobject) {
            return pattern.matcher(pGobject.toString()).matches();
        }
    }

    Condition<PgArray> pgArrayContainsExactly(Object... array) {
        var p = new PgArrayEquals(array);
        return new Condition<>(p, p.toString());
    }

    static class PgArrayEquals implements Predicate<PgArray> {

        private final String expected;

        public PgArrayEquals(Object[] expected) {
            this.expected = "{" + Stream.of(expected).map(Object::toString).collect(Collectors.joining(",")) + "}";
        }

        @Override
        public boolean test(PgArray pgArray) {
            return expected.equals(pgArray.toString());
        }

        public String toString() {
            return this.expected;
        }
    }

}
