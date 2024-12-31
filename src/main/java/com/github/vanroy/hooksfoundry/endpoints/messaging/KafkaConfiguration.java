package com.github.vanroy.hooksfoundry.endpoints.messaging;

import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventDeserializer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;

import java.util.Map;
import java.util.regex.Pattern;

@Configuration
public class KafkaConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, CloudEvent> test(KafkaTopicListener listener) {
        ContainerProperties containerProps = new ContainerProperties(Pattern.compile("orders"));
        containerProps.setMessageListener(listener);

        DefaultKafkaConsumerFactory<String, CloudEvent> cf =
                new DefaultKafkaConsumerFactory<>(consumerProps(),
                        new StringDeserializer(),
                        new CloudEventDeserializer()
                );

        ConcurrentMessageListenerContainer<String, CloudEvent> container =
                new ConcurrentMessageListenerContainer<>(cf, containerProps);

        container.setConcurrency(10);
        container.setCommonErrorHandler();

        return container;
    }

    private Map<String, Object> consumerProps() {
        return Map.of(
                CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                CommonClientConfigs.GROUP_ID_CONFIG, "hooks-foundry",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                "auto-offset-reset", "earliest",
                "partition.assignment.strategy", "org.apache.kafka.clients.consumer.RoundRobinAssignor",
                "metadata.max.age.ms", 1000
        );
    }
}
