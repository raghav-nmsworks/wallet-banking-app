package com.nextuple.walletbankingapp.dto.response;

import com.nextuple.walletbankingapp.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPageResponseDTO {
    private List<Transaction> transactions;
    private int totalPage;
}
