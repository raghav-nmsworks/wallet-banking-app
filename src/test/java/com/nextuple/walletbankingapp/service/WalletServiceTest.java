package com.nextuple.walletbankingapp.service;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.walletbankingapp.entity.Wallet;
import com.nextuple.walletbankingapp.repository.WalletRepository;
import com.nextuple.walletbankingapp.service.UserService;
import com.nextuple.walletbankingapp.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetWalletById() {
        String walletId = "123";
        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        Optional<Wallet> resultWallet = walletService.getWalletById(walletId);

        assertTrue(resultWallet.isPresent());
        assertEquals(walletId, resultWallet.get().getId());

        verify(walletRepository, times(1)).findById(walletId);
    }

    @Test
    public void testGetWalletByIdWhenNotFound() {
        String walletId = "456";

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        Optional<Wallet> resultWallet = walletService.getWalletById(walletId);

        assertFalse(resultWallet.isPresent());

        verify(walletRepository, times(1)).findById(walletId);
    }

    @Test
    public void testCreateWallet() {
        Wallet wallet = new Wallet();

        when(walletRepository.save(wallet)).thenReturn(wallet);

        Wallet createdWallet = walletService.createWallet(wallet);

        assertNotNull(createdWallet);

        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    public void testUpdateWallet() {
        String walletId = "789";
        Wallet updatedWallet = new Wallet();
        updatedWallet.setId(walletId);

        when(walletRepository.existsById(walletId)).thenReturn(true);
        when(walletRepository.save(updatedWallet)).thenReturn(updatedWallet);

        Wallet resultWallet = walletService.updateWallet(walletId, updatedWallet);

        assertNotNull(resultWallet);
        assertEquals(walletId, resultWallet.getId());

        verify(walletRepository, times(1)).existsById(walletId);
        verify(walletRepository, times(1)).save(updatedWallet);
    }

    @Test
    public void testUpdateWalletWhenNotFound() {
        String walletId = "456";
        Wallet updatedWallet = new Wallet();

        when(walletRepository.existsById(walletId)).thenReturn(false);

        Wallet resultWallet = walletService.updateWallet(walletId, updatedWallet);
        assertNull(resultWallet);
        verify(walletRepository, times(1)).existsById(walletId);
        verify(walletRepository, never()).save(updatedWallet);
    }
}
