package io.project.resources;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    private static final AtomicInteger requestCounter = new AtomicInteger();
    private static final ArrayBlockingQueue<String> SERVER_QUEUE = new ArrayBlockingQueue<String>(3);
    static{
        SERVER_QUEUE.add("S1"); SERVER_QUEUE.add("S2"); SERVER_QUEUE.add("S3");
    }

    @GetMapping("/action")
    public ResponseEntity<String> action(@RequestParam(required = false) String value) throws InterruptedException {
        int requestId = requestCounter.incrementAndGet();

        String serverId = SERVER_QUEUE.poll();
        try {
            Thread.sleep(1000);

            if (serverId == null) {
                logger.info("#{} REQUEST-{}({}) TOO_MANY_REQUESTS", "#", requestId, value);
                return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
            } else {
                long time = System.currentTimeMillis();
                logger.info("#{} REQUEST-{}({}) @{} ", serverId, requestId, value == null ? "<null>" : value, time);
                return new ResponseEntity<>("[REPLY-"+ serverId + "/" + requestId +"] " + (value == null ? "<null>" : value) + " @(" + time + ")", HttpStatus.OK);
            }
        } finally {
            if (serverId != null) {
                SERVER_QUEUE.put(serverId);
            }
        }
    }
}
