package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MidasListener {

    public MidasListener() {
        System.out.println("MidasListener created!");
    }

   @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void receiveTransactions(Transaction transaction) {
       System.out.println("KAFKA LISTENER TRIGGERED!");
       System.out.println("Received transaction: " + transaction);

       int breakpoint = 1;
   }

}
