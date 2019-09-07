package io.project.resources;

import io.project.model.UserRequest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author armena
 */
@RestController
@RequestMapping("/api/test")
public class InfoController {

    private static final Logger logger = LoggerFactory.getLogger(InfoController.class);

    private static final AtomicInteger syncCounter = new AtomicInteger();
    private static final AtomicInteger asyncCounter = new AtomicInteger();

    @Autowired
    private ReplyingKafkaTemplate<String, UserRequest, UserRequest> kafkaTemplate;

    @Value("${kafka.topic.request-topic}")
    private String requestTopic;

    @Value("${kafka.topic.requestreply-topic}")
    private String requestReplyTopic;

    @GetMapping("/sync")
    public ResponseEntity<String> sync(@RequestParam(required = false) String value) throws InterruptedException {

        final int requestId = syncCounter.incrementAndGet();
        final String uri = "http://localhost:2010/api/test/action?value=" + (value == null ? requestId : value);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.getForObject(uri, String.class);
            logger.info("SYNC-REQUEST[{}]({}) OK: {}", requestId, value, result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RestClientException ex) {
            logger.info("SYNC-REQUEST[{}]({}) FAILED: {}", requestId, value, ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @GetMapping("/async")
    public ResponseEntity<String> async(@RequestParam(required = false) String value) throws InterruptedException, ExecutionException {
        final int requestId = asyncCounter.incrementAndGet();

        UserRequest producerRequest = new UserRequest();
        producerRequest.setValue(value == null ? (requestId + "") : value);
        // create producer record
        ProducerRecord<String, UserRequest> record = new ProducerRecord<>(requestTopic, producerRequest);
        // set reply topic in header
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));
        // post in kafka topic
        RequestReplyFuture<String, UserRequest, UserRequest> sendAndReceive = kafkaTemplate.sendAndReceive(record);

        // confirm if producer produced successfully
        SendResult<String, UserRequest> sendResult = sendAndReceive.getSendFuture().get();

        //print all headers
        sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));

        // get consumer record
        ConsumerRecord<String, UserRequest> consumerRecord = sendAndReceive.get();

        logger.info("ASync-REQUEST[{}]({}) {}: {}", requestId, value, consumerRecord.value().getStatus(), consumerRecord.value().getValue());
        // return consumer value
        return new ResponseEntity<>(consumerRecord.value().getValue(), consumerRecord.value().getStatus());
    }

}
