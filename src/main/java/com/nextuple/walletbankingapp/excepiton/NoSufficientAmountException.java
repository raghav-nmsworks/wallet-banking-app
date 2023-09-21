package com.nextuple.walletbankingapp.excepiton;

public class NoSufficientAmountException extends RuntimeException{
    public NoSufficientAmountException(String msg){
        super(msg);
    }
}
