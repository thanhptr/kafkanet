package io.project.resources;

import io.project.model.UserRequest;

import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Autowired
    private ReplyingKafkaTemplate<String, UserRequest, UserRequest> kafkaTemplate;

    @Value("${kafka.topic.request-topic}")
    private String requestTopic;

    @Value("${kafka.topic.requestreply-topic}")
    private String requestReplyTopic;

    @GetMapping("/sync")
    public ResponseEntity<String> sync(@RequestParam(required = false) String value) throws InterruptedException {

        final String uri = "http://localhost:2010/api/test/action?value=" + (value == null ? "" : value);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.getForObject(uri, String.class);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RestClientException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @GetMapping("/async")
    public ResponseEntity<String> async(@RequestParam(required = false) String value) throws InterruptedException, ExecutionException {
        UserRequest producerRequest = new UserRequest();
        producerRequest.setValue(value);
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
        // return consumer value
        return new ResponseEntity<>(consumerRecord.value().getValue(), consumerRecord.value().getStatus());
    }

}
