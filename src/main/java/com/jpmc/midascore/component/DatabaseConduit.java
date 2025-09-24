package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
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
        
        // Update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);
        
        // Save updated user records
        userRepository.save(sender);
        userRepository.save(recipient);
        
        // Create and save transaction record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount);
        transactionRepository.save(transactionRecord);
        
        System.out.println("Transaction processed: " + sender.getName() + " -> " + recipient.getName() + " amount: " + amount);
    }
}
