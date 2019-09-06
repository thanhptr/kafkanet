#!/usr/bin/env bash
java -jar -Dserver.port=2002 -Dkafka.consumergroup=d2.checkout-ticket.confirm-reply.qc-loadtest2 ../producer/target/producer.jar