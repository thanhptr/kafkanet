#!/usr/bin/env bash
java -jar -Dserver.port=2011 -Dkafka.consumergroup=d2.checkout-ticket.confirm-request.x-loadtest0 -Dkafka.topic.request-topic=d2.checkout-ticket.confirm-request.x -Dkafka.topic.requestreply-topic=d2.checkout-ticket.confirm-reply.x ../consumer/target/consumer.jar
