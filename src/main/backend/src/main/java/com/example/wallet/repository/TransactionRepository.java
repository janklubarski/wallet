package com.example.wallet.repository;

import com.example.wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWalletIdOrderByTransactionDateDesc(Long walletId);
    List<Transaction> findByWalletIdAndTransactionDateBetweenOrderByTransactionDateDesc(Long walletId, LocalDateTime from, LocalDateTime to);
    List<Transaction> findByWalletIdAndTransactionDateAfterOrderByTransactionDateDesc(Long walletId, LocalDateTime from);
    List<Transaction> findByWalletIdAndTransactionDateBeforeOrderByTransactionDateDesc(Long walletId, LocalDateTime to);
    
}
