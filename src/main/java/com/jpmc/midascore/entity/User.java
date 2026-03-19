package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private float balance;

    public User() {}

    public User(String name, float balance) {
        this.name = name;
        this.balance = balance;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public float getBalance() { return balance; }
    public void setBalance(float balance) { this.balance = balance; }
}
