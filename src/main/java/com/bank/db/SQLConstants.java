package com.bank.db;

public class SQLConstants {
    // User Related Queries
    public static final String REGISTER_USER = "INSERT INTO users (account_number, full_name, security_pin, balance) VALUES (?, ?, ?, ?)";
    public static final String LOGIN_USER = "SELECT * FROM users WHERE account_number = ? AND security_pin = ?";
    public static final String GET_BALANCE = "SELECT balance FROM users WHERE account_number = ?";
    public static final String DEBIT_BALANCE =  "UPDATE users SET balance = balance - ? WHERE account_number = ?";
    public static final String RECORD_TRANSACTION_DEBIT = "INSERT INTO transactions (receiver_acc, amount, type) VALUES (?, ?, 'DEPOSIT')";
    public static final String RECORD_TRANSACTION_WITHDRAW = "INSERT INTO transactions (sender_acc, amount, type) VALUES (?, ?, 'WITHDRAW')";

    // Account & Transaction Related Queries
    public static final String UPDATE_BALANCE = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
    public static final String INSERT_TRANSACTION = "INSERT INTO transactions (sender_acc, receiver_acc, amount, type) VALUES (?, ?, ?, 'TRANSFER')";
    public static final String MINI_STATEMENT = "SELECT * FROM transactions WHERE sender_acc = ? OR receiver_acc = ? ORDER BY timestamp DESC LIMIT 5";
}