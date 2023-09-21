package com.nextuple.walletbankingapp.controller;

import com.nextuple.walletbankingapp.dto.request.RechargeRequest;
import com.nextuple.walletbankingapp.dto.request.TransactionRequestDTO;
import com.nextuple.walletbankingapp.entity.Transaction;
import com.nextuple.walletbankingapp.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllTransactions() {
        String token = "Bearer mockToken";
        List<Transaction> mockTransactions = new ArrayList<>();
        mockTransactions.add(new Transaction());
        mockTransactions.add(new Transaction());
        String to=token.substring(7);
        when(transactionService.getAllTransactions(to)).thenReturn(mockTransactions);

        ResponseEntity<List<Transaction>> responseEntity = transactionController.getAllTransactions(token);
        System.out.println("------------------" + responseEntity.getBody());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<Transaction> responseTransactions = responseEntity.getBody();
        assertNotNull(responseTransactions);
        assertEquals(2, responseTransactions.size());

        verify(transactionService, times(1)).getAllTransactions(to);
    }

    @Test
    public void testTransfer() {
        String token = "Bearer mockToken";
        TransactionRequestDTO mockTransactionRequest = new TransactionRequestDTO();
        Transaction mockTransaction = new Transaction();
        String tok=token.substring(7);
        when(transactionService.transfer(tok, mockTransactionRequest)).thenReturn(mockTransaction);

        ResponseEntity<Transaction> responseEntity = transactionController.transfer(token, mockTransactionRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Transaction responseTransaction = responseEntity.getBody();
        assertNotNull(responseTransaction);

        verify(transactionService, times(1)).transfer(tok, mockTransactionRequest);
    }

    @Test
    public void testRecharge() {
        String token = "Bearer mockToken";
        RechargeRequest mockRechargeRequest = new RechargeRequest();
        Transaction mockTransaction = new Transaction();
        String tok=token.substring(7);
        when(transactionService.recharge(tok, mockRechargeRequest)).thenReturn(mockTransaction);

        ResponseEntity<Transaction> responseEntity = transactionController.recharge(token, mockRechargeRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Transaction responseTransaction = responseEntity.getBody();
        assertNotNull(responseTransaction);

        verify(transactionService, times(1)).recharge(tok, mockRechargeRequest);
    }

}
