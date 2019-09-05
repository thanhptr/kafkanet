package io.project.resources;

import io.project.model.TestRequest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import io.project.model.TestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private AtomicInteger counter = new AtomicInteger();

    @ResponseBody
    @PostMapping(value = "/action", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TestResponse> action(@RequestBody TestRequest request) throws InterruptedException, ExecutionException {
        int currentCounter = counter.incrementAndGet();
        if (currentCounter >= 3) {
            // Error
            counter.decrementAndGet();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            try {
                Thread.sleep(1000);
                TestResponse testResponse = new TestResponse();
                testResponse.setValue("[REPLY-"+ currentCounter +"] " + request.getValue() + " @(" + System.currentTimeMillis() + ")");
                return new ResponseEntity<>(testResponse, HttpStatus.OK);
            } finally {
                counter.decrementAndGet();
            }
        }
    }
}
