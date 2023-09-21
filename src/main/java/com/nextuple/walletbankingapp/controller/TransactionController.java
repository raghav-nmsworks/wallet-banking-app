package com.nextuple.walletbankingapp.controller;

import com.nextuple.walletbankingapp.dto.request.RechargeRequest;
import com.nextuple.walletbankingapp.dto.request.TransactionRequestDTO;
import com.nextuple.walletbankingapp.dto.response.TransactionPageResponseDTO;
import com.nextuple.walletbankingapp.entity.Transaction;
import com.nextuple.walletbankingapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/all-transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        List<Transaction> transactions = transactionService.getAllTransactions(token);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions-page")
    public ResponseEntity<TransactionPageResponseDTO> getTransactionPage(@RequestHeader("Authorization") String token, @RequestParam int page, @RequestParam int size) {
        token = token.substring(7);
        TransactionPageResponseDTO transactionPageResponseDTO = transactionService.getTransactionsPage(token,page,size);
        return ResponseEntity.ok(transactionPageResponseDTO);
    }


    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@RequestHeader("Authorization") String token, @RequestBody TransactionRequestDTO transactionRequestDTO) {
        System.out.println("controller");
        token = token.substring(7);
        Transaction createdTransaction = transactionService.transfer(token, transactionRequestDTO);
        return ResponseEntity.ok(createdTransaction);
    }

    @PostMapping("/recharge")
    public ResponseEntity<Transaction> recharge(@RequestHeader("Authorization") String token, @RequestBody RechargeRequest rechargeRequest) {
        token = token.substring(7);
        Transaction createdTransaction = transactionService.recharge(token, rechargeRequest);
        return ResponseEntity.ok(createdTransaction);
    }

}

