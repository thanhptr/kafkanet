package io.project.resources;

import io.project.model.TestRequest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import io.project.model.TestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private AtomicInteger counter = new AtomicInteger();

    @GetMapping("/action")
    public ResponseEntity<String> action(@RequestParam(required = false) String value) throws InterruptedException {
        if (counter.intValue() >= 3) {
            Thread.sleep(1000);
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        } else {
            int currentCounter = counter.incrementAndGet();
            if (currentCounter > 3) {
                // Error
                counter.decrementAndGet();
                Thread.sleep(1000);
                return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
            } else {
                try {
                    Thread.sleep(1000);
                    return new ResponseEntity<>(
                            "[REPLY-"+ currentCounter +"] " + (value == null ? "<null>" : value) + " @(" + System.currentTimeMillis() + ")",
                            HttpStatus.OK
                    );
                } finally {
                    counter.decrementAndGet();
                }
            }
        }
    }
}
