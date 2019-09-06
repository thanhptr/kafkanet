#!/usr/bin/env bash
java -jar -Dserver.port=2001 -Dkafka.consumergroup=d2.checkout-ticket.confirm-reply.qc-loadtest1 ../producer/target/producer.jar