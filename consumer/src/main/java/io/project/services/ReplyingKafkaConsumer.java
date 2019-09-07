package io.project.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import io.project.model.UserRequest;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
public class ReplyingKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ReplyingKafkaConsumer.class);

    @KafkaListener(topics = "${kafka.topic.request-topic}")
    @SendTo
    public UserRequest listen(UserRequest request) throws InterruptedException {
        final String uri = "http://localhost:2010/api/test/action?value=" + (request == null || request.getValue() == null ? "" : request.getValue());

        UserRequest response = new UserRequest();
        RestTemplate restTemplate = new RestTemplate();
        try {
            response.setValue(restTemplate.getForObject(uri, String.class));
            logger.info("RECEIVED-REQUEST OK: {}", response.getValue());
            response.setStatus(HttpStatus.OK);
        } catch (RestClientException ex) {
            logger.info("RECEIVED-REQUEST FAILED: {} -> {}", request.getValue(), ex.getMessage());
            response.setValue(ex.getMessage());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS);
        }
        return response;
    }
}
