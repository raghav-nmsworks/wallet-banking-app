package com.nextuple.walletbankingapp.service;

import com.nextuple.walletbankingapp.dto.request.RechargeRequest;
import com.nextuple.walletbankingapp.dto.request.TransactionRequestDTO;
import com.nextuple.walletbankingapp.dto.response.TransactionPageResponseDTO;
import com.nextuple.walletbankingapp.entity.Transaction;
import com.nextuple.walletbankingapp.entity.User;
import com.nextuple.walletbankingapp.entity.Wallet;
import com.nextuple.walletbankingapp.excepiton.InvalidRechargeRequestException;
import com.nextuple.walletbankingapp.excepiton.NoSufficientAmountException;
import com.nextuple.walletbankingapp.jwt.JwtHelper;
import com.nextuple.walletbankingapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.nextuple.walletbankingapp.entity.Transaction.TransactionType;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private WalletService walletService;

    @Autowired
    private EmailSenderService emailSenderService;


    public List<Transaction> getAllTransactions(String token) {
        User currentuser = userService.getUserByEmail(jwtHelper.getUsernameFromToken(token));
        return transactionRepository.findAllByWalletId(currentuser.getWallet().getId());
    }

    public TransactionPageResponseDTO getTransactionsPage(String token, int page, int size){
        User currentuser = userService.getUserByEmail(jwtHelper.getUsernameFromToken(token));
        if (currentuser == null){
            throw new UsernameNotFoundException("User not exits");
        }
        Sort sortByDateDesc = Sort.by(Sort.Direction.DESC, "timestamp");
        Pageable paging = PageRequest.of(page, size, sortByDateDesc);
        Page<Transaction> transactionPage = transactionRepository.findAllByWalletId(currentuser.getWallet().getId(),paging);
        TransactionPageResponseDTO transactionPageResponse = new TransactionPageResponseDTO();
        transactionPageResponse.setTransactions(transactionPage.getContent());
        transactionPageResponse.setTotalPage(transactionPage.getTotalPages());
        return transactionPageResponse;
    }



    @Transactional
    public Transaction recharge(String token, RechargeRequest rechargeRequest){
        if (rechargeRequest.getAmount() <= 0){
            throw new InvalidRechargeRequestException("Invalid Recharge Amount ");
        }
        String email = jwtHelper.getUsernameFromToken(token);
        User user = userService.getUserByEmail(email);
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setTransctionMethod(Transaction.TransactionMethod.RECHARGE);
        transaction.setAmount(rechargeRequest.getAmount());
        transaction.setReceiver(user);
        transaction.setWalletId(user.getEmail());
        transaction.setTimestamp(new Date());
        Wallet userWallet = user.getWallet();
        userWallet.setBalance(userWallet.getBalance() + rechargeRequest.getAmount());
        walletService.updateWallet(userWallet.getId(),userWallet);
        transactionRepository.save(transaction);

        String emailBody = "INR : " + rechargeRequest.getAmount() + " is credited to you account. \nYour current wallet balance is  " + userWallet.getBalance() + ".";
        System.out.println("user email : " + user.getEmail());
        emailSenderService.sendSimpleEmail(user.getEmail(),emailBody,"Wallet Reacharge");
        return transaction;
    }

    @Transactional
    public Transaction transfer(String token, TransactionRequestDTO transactionRequestDTO) {
        System.out.println(transactionRequestDTO.getReceiverEmail());
        User sender = userService.getUserByEmail(jwtHelper.getUsernameFromToken(token));
        User receiver = userService.getUserByEmail(transactionRequestDTO.getReceiverEmail());
        if (receiver == null){
            // receiver not found
            throw new UsernameNotFoundException("No receiver exists with this email : " + transactionRequestDTO.getReceiverEmail());
        }
        if(sender.getWallet().getBalance()< transactionRequestDTO.getAmount())
        {
           throw new NoSufficientAmountException("Insufficient Balance: " + sender.getWallet().getBalance());
        }
        double amount = transactionRequestDTO.getAmount();
        Transaction debitTransaction = new Transaction();
        debitTransaction.setSender(sender);
        debitTransaction.setTransactionType(TransactionType.DEBIT);
        debitTransaction.setTransctionMethod(Transaction.TransactionMethod.TRANSFER);
        debitTransaction.setAmount(amount);
        debitTransaction.setReceiver(receiver);
        debitTransaction.setWalletId(sender.getEmail());
        debitTransaction.setTimestamp(new Date());
        Wallet senderWallet = sender.getWallet();
        senderWallet.setBalance(senderWallet.getBalance() - amount);
        walletService.updateWallet(senderWallet.getId(),senderWallet);
        transactionRepository.save(debitTransaction);
        String debitTransactionBody = "INR " +transactionRequestDTO.getAmount() + " is debited from your account "
                + "\nYour current balance is INR " + senderWallet.getBalance();
        emailSenderService.sendSimpleEmail(sender.getEmail(), debitTransactionBody,"Wallet Amount Debit");
        // for credit transaction
        Transaction creditTransaction = new Transaction();

        creditTransaction.setSender(sender);
        creditTransaction.setTransactionType(TransactionType.CREDIT);
        creditTransaction.setTransctionMethod(Transaction.TransactionMethod.TRANSFER);
        creditTransaction.setAmount(amount);
        creditTransaction.setReceiver(receiver);
        creditTransaction.setWalletId(receiver.getEmail());
        creditTransaction.setTimestamp(new Date());
        Wallet receiverWallet = receiver.getWallet();
        receiverWallet.setBalance(receiverWallet.getBalance() + amount);
        creditTransaction.setWalletId(receiverWallet.getId());

        walletService.updateWallet(receiverWallet.getId(),receiverWallet);
        transactionRepository.save(creditTransaction);
        String creditTransactionBody = "INR " +transactionRequestDTO.getAmount() + " is credited from your account "
                + "\nYour current balance is INR " + receiverWallet.getBalance();
        emailSenderService.sendSimpleEmail(receiver.getEmail(), creditTransactionBody,"Wallet Amount Credit");
        return debitTransaction;
    }

    // Add custom methods as needed
}

