package com.nextuple.walletbankingapp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "transactions")
public class Transaction {


    public enum TransactionType{
        CREDIT,
        DEBIT
    }
    public enum TransactionMethod{
        RECHARGE,
        TRANSFER
    }

    @Id
    private String id;
    private String walletId;
    private double amount;
    private Date timestamp;
    private TransactionType transactionType;
    private TransactionMethod transctionMethod;
    @DBRef
    private User sender;
    @DBRef
    private User receiver;
}

