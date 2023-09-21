package com.nextuple.walletbankingapp.service;


import com.nextuple.walletbankingapp.dto.request.RechargeRequest;
import com.nextuple.walletbankingapp.dto.request.TransactionRequestDTO;
import com.nextuple.walletbankingapp.entity.Transaction;
import com.nextuple.walletbankingapp.entity.User;
import com.nextuple.walletbankingapp.entity.Wallet;
import com.nextuple.walletbankingapp.jwt.JwtHelper;
import com.nextuple.walletbankingapp.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private WalletService walletService;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRecharge() {
        String token = "Bearer token";
        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setAmount(100.0);

        User user = new User();
        user.setEmail("user@example.com");
        Wallet userWallet = new Wallet();
        userWallet.setId("user@example.com");
        user.setWallet(userWallet);



        when(jwtHelper.getUsernameFromToken(token)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        Transaction savedTransaction = new Transaction();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        Transaction transaction = transactionService.recharge(token, rechargeRequest);

        assertNotNull(transaction);
        assertEquals(Transaction.TransactionType.CREDIT, transaction.getTransactionType());
        assertEquals(Transaction.TransactionMethod.RECHARGE, transaction.getTransctionMethod());
        assertEquals(100.0, transaction.getAmount());
        assertEquals(user.getEmail(), transaction.getReceiver().getEmail());
        assertEquals(user.getEmail(), transaction.getWalletId());
        assertNotNull(transaction.getTimestamp());
    }

    @Test
    public void testTransfer() {
        String token = "token";
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setReceiverEmail("receiver@example.com");
        transactionRequestDTO.setAmount(50.0);

        User sender = new User();
        sender.setEmail("sender@example.com");
        Wallet senderWallet = new Wallet();
        senderWallet.setId("sender@example.com");
        senderWallet.setBalance(100.0);
        sender.setWallet(senderWallet);


        User receiver = new User();
        receiver.setEmail("receiver@example.com");
        Wallet receiverWallet = new Wallet();
        receiverWallet.setId("receiver@example.com");
        receiver.setWallet(receiverWallet);

        when(jwtHelper.getUsernameFromToken(token)).thenReturn(sender.getEmail());
        when(userService.getUserByEmail(sender.getEmail())).thenReturn(sender);
        when(userService.getUserByEmail(receiver.getEmail())).thenReturn(receiver);

        Transaction savedDebitTransaction = new Transaction();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedDebitTransaction);

        Transaction savedCreditTransaction = new Transaction();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedCreditTransaction);

        Transaction debitTransaction = transactionService.transfer(token, transactionRequestDTO);

        assertNotNull(debitTransaction);
        assertEquals(Transaction.TransactionType.DEBIT, debitTransaction.getTransactionType());
        assertEquals(Transaction.TransactionMethod.TRANSFER, debitTransaction.getTransctionMethod());
        assertEquals(50.0, debitTransaction.getAmount());
        assertEquals(receiver.getEmail(), debitTransaction.getReceiver().getEmail());
        assertEquals(sender.getEmail(), debitTransaction.getWalletId());
        assertNotNull(debitTransaction.getTimestamp());

    }

    @Test
    public void testTransferReceiverNotFound() {
        String token = "token";
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setReceiverEmail("nonexistent@example.com");
        transactionRequestDTO.setAmount(50.0);

        User sender = new User();
        sender.setEmail("sender@example.com");
        Wallet senderWallet = new Wallet();
        senderWallet.setId("sender@example.com");
        senderWallet.setBalance(100.0);
        sender.setWallet(senderWallet);

        when(jwtHelper.getUsernameFromToken(token)).thenReturn(sender.getEmail());
        when(userService.getUserByEmail(sender.getEmail())).thenReturn(sender);
        when(userService.getUserByEmail("nonexistent@example.com")).thenReturn(null);


        assertThrows(UsernameNotFoundException.class, () -> transactionService.transfer(token, transactionRequestDTO));
    }

    @Test
    public void testTransferInsufficientBalance() {

        String token = "token";
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setReceiverEmail("receiver@example.com");
        transactionRequestDTO.setAmount(150.0);

        User sender = new User();
        sender.setEmail("sender@example.com");
        Wallet senderWallet = new Wallet();
        senderWallet.setId("sender@example.com");
        senderWallet.setBalance(100.0);
        sender.setWallet(senderWallet);

        User receiver = new User();
        receiver.setEmail("receiver@example.com");

        when(jwtHelper.getUsernameFromToken(token)).thenReturn(sender.getEmail());
        when(userService.getUserByEmail(sender.getEmail())).thenReturn(sender);
        when(userService.getUserByEmail(receiver.getEmail())).thenReturn(receiver);

        Transaction result = transactionService.transfer(token, transactionRequestDTO);
        assertNull(result);
    }

    @Test
    public void testGetAllTransactions() {
        String token = "token";
        User user = new User();
        Wallet userWallet = new Wallet();
        user.setWallet(userWallet);

        when(jwtHelper.getUsernameFromToken(token)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        List<Transaction> transactions = new ArrayList<>();
        when(transactionRepository.findAllByWalletId(userWallet.getId())).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllTransactions(token);

        assertEquals(transactions, result);
    }
}
