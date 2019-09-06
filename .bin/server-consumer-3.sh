#!/usr/bin/env bash
java -jar -Dserver.port=2013 -Dkafka.consumergroup=d2.checkout-ticket.confirm-request.qc-loadtest0 ../consumer/target/consumer.jar