package com.nextuple.walletbankingapp.excepiton;

public class InvalidRechargeRequestException extends RuntimeException{
    public InvalidRechargeRequestException(String msg){
        super(msg);
    }
}
