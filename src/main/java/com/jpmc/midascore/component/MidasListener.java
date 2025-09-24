package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MidasListener {

    private final DatabaseConduit databaseConduit;

    @Autowired
    public MidasListener(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
        System.out.println("MidasListener created!");
    }

   @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void receiveTransactions(Transaction transaction) {
       System.out.println("KAFKA LISTENER TRIGGERED!");
       System.out.println("Received transaction: " + transaction);

       databaseConduit.processTransaction(transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());
   }

}
