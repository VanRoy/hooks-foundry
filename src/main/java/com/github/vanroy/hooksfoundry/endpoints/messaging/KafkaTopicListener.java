package com.github.vanroy.hooksfoundry.endpoints.messaging;

import com.github.vanroy.hooksfoundry.domain.models.InputEvent;
import com.github.vanroy.hooksfoundry.domain.usecases.ProcessInputEventUseCase;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.ContentType;
import io.cloudevents.core.provider.EventFormatProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaTopicListener implements MessageListener<String, CloudEvent> {

    private final ProcessInputEventUseCase processUseCase;

    @Bean
    public DefaultErrorHandler errorHandler() {
        BackOff fixedBackOff = new FixedBackOff(500, 1);
        return new DefaultErrorHandler((consumerRecord, exception) -> {
            log.error("Couldn't process message: {}", consumerRecord.value().toString(), exception);
        }, fixedBackOff);
    }

    //  org. apache. kafka. clients. CommonClientConfigs. METADATA_MAX_AGE_CONFIG

//    @KafkaListener(id = "id", topicPattern = "orders", concurrency = "10"/*,  contentTypeConverter = "cloudEventMessageConverter"*/)
  //  public void listener(@Payload CloudEvent event, @Headers Map<String, Object> headers) { //, @Headers MessageHeaders rawHeaders) {
    @Override
    public void onMessage(ConsumerRecord<String, CloudEvent> data) {
        try {
            var inputEvent = toJsonFormat(data.value());
            this.processUseCase.execute(inputEvent);
        } catch (IOException e) {
            log.error("Invalid event format, not match with CloudEvent", e);
        }
    }

    private InputEvent toJsonFormat(CloudEvent event) throws IOException {
        var jsonEvent = EventFormatProvider
                .getInstance()
                .resolveFormat(ContentType.JSON)
                .serialize(event);
        return InputEvent.of(jsonEvent);
    }


/*
    private static final Pattern JSON_CONTENT_TYPE_PATTERN = Pattern.compile("^(application|text)\\/([a-zA-Z]+\\+)?json(;.*)*$");
    static boolean dataIsJsonContentType(String contentType) {
        // If content type, spec states that we should assume is json
        return contentType == null || JSON_CONTENT_TYPE_PATTERN.matcher(contentType).matches();
    }

    private String handleData(CloudEvent inputEvent) {

        if(dataIsJsonContentType(inputEvent.getDataContentType())&& inputEvent.getData() != null) {
            return new String(inputEvent.getData().toBytes());
        }
        return "";
    }*/
}
