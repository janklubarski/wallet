package com.example.wallet.repository;

import com.example.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByAppUserIdAndWalletName(Long appUserId, String walletName);
    List<Wallet> findByAppUserId(Long appUserId);
    
}
