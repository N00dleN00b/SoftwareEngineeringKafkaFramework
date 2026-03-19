package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.User;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private final DatabaseConduit databaseConduit;
    private final TransactionRecordRepository transactionRecordRepository;
    private final IncentiveService incentiveService;

    public TransactionService(DatabaseConduit databaseConduit, 
                              TransactionRecordRepository transactionRecordRepository,
                              IncentiveService incentiveService) {
        this.databaseConduit = databaseConduit;
        this.transactionRecordRepository = transactionRecordRepository;
        this.incentiveService = incentiveService;
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        User sender = databaseConduit.getUserById(transaction.getSenderId());
        User recipient = databaseConduit.getUserById(transaction.getRecipientId());

        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {
            Incentive incentive = incentiveService.getIncentive(transaction);
            float bonus = (incentive != null) ? incentive.getAmount() : 0f;

            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + bonus);

            databaseConduit.saveUser(sender);
            databaseConduit.saveUser(recipient);
            transactionRecordRepository.save(new TransactionRecord(sender, recipient, transaction.getAmount()));
        }
    }
}
