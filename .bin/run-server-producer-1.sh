#!/usr/bin/env bash
java -jar -Dserver.port=2001 -Dkafka.consumergroup=d2.checkout-ticket.confirm-reply.local-test2001 ../producer/target/producer.jar