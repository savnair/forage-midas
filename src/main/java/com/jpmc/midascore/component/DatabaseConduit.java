package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.service.IncentiveService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveService incentiveService;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository, IncentiveService incentiveService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveService = incentiveService;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public UserRecord findUserById(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void processTransaction(long senderId, long recipientId, float amount) {
        UserRecord sender = findUserById(senderId);
        UserRecord recipient = findUserById(recipientId);
        
        if (sender == null || recipient == null) {
            System.out.println("Transaction discarded: Invalid sender or recipient ID");
            return;
        }
        
        if (sender.getBalance() < amount) {
            System.out.println("Transaction discarded: Insufficient balance for sender " + sender.getName());
            return;
        }

        // Call incentive API after validation
        Transaction tx = new Transaction(senderId, recipientId, amount);
        double incentiveAmount = incentiveService.getIncentive(tx);
        float incentive = (float) Math.max(0.0, incentiveAmount);

        // Update balances (do not deduct incentive from sender)
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentive);
        
        // Save updated user records
        userRepository.save(sender);
        userRepository.save(recipient);
        
        // Create and save transaction record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount, incentive);
        transactionRepository.save(transactionRecord);
        
        System.out.println("Transaction processed: " + sender.getName() + " -> " + recipient.getName() + " amount: " + amount + ", incentive: " + incentive);
    }
}
