#!/usr/bin/env bash
java -jar -Dserver.port=2011 -Dkafka.consumergroup=d2.checkout-ticket.confirm-request.local-test0 ../consumer/target/consumer.jar