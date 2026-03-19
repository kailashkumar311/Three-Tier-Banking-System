package com.bank.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class Transaction {
    private int id;
    private Long senderAcc;
    private Long receiverAcc;
    private double amount;
    private String type;
    private Timestamp timestamp;

}
