package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User recipient;

    private float amount;

    public TransactionRecord() {}

    public TransactionRecord(User sender, User recipient, float amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    public User getSender() { return sender; }
    public User getRecipient() { return recipient; }
    public float getAmount() { return amount; }
}
