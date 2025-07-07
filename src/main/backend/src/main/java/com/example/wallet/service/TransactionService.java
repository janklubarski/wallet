package com.example.wallet.service;

import com.example.wallet.dto.TransactionDTO;
import com.example.wallet.dto.TransferRequestDTO;
import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.Wallet;
import com.example.wallet.entity.enums.TransactionStatusEnum;
import com.example.wallet.exception.InsufficientFundsException;
import com.example.wallet.exception.InvalidTransactionException;
import com.example.wallet.exception.WalletNotFoundException;
import com.example.wallet.mapper.TransactionMapper;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionDTO createPendingTransaction(TransferRequestDTO dto) {
        Wallet senderWallet = walletRepository.findById(dto.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Recipient wallet not found"));

        Transaction.TransactionBuilder transactionBuilder = Transaction.builder()
                .wallet(senderWallet)
                .amount(dto.getAmount())
                .currency(dto.getCurrency())
                .transactionType(dto.getTransactionType())
                .description(dto.getDescription())
                .variableSymbol(dto.getVariableSymbol())
                .status(TransactionStatusEnum.PENDING)
                .transactionDate(LocalDateTime.now());

        switch (dto.getTransactionType()) {
            case TRANSFER -> {
                if (dto.getReceiverWalletId() == null) {
                    throw new InvalidTransactionException("receiverWalletId is required for TRANSFER");
                }
                transactionBuilder.destinationWalletId(dto.getReceiverWalletId());
            }

            case WITHDRAWAL -> {
                if (dto.getBankAccountNumber() == null || dto.getBankAccountNumber().isBlank()) {
                    throw new InvalidTransactionException("bankAccountNumber is required for WITHDRAWAL");
                }
                transactionBuilder.bankAccountNumber(dto.getBankAccountNumber());
            }

            case INCOME -> {
                // No additional fields needed for loading funds
            }

            default -> throw new InvalidTransactionException("Unsupported transaction type: " + dto.getTransactionType());
        }

        Transaction transaction = transactionRepository.save(transactionBuilder.build());
        return transactionMapper.toDTO(transaction);
    }

    @Transactional
    public void confirmTransaction(Long transactionId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new InvalidTransactionException("Transaction with ID " + transactionId + " not found"));

        if (!tx.getStatus().equals(TransactionStatusEnum.PENDING)) {
            throw new InvalidTransactionException("Only pending transactions can be confirmed");
        }

        Wallet wallet = tx.getWallet();
        BigDecimal amount = tx.getAmount();

        switch (tx.getTransactionType()) {
            case INCOME -> {
                wallet.setBalance(wallet.getBalance().add(amount));
            }

            case WITHDRAWAL -> {
                if (wallet.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException("Insufficient funds for withdrawal");
                }
                wallet.setBalance(wallet.getBalance().subtract(amount));
            }

            case TRANSFER -> {
                Wallet receiverWallet = walletRepository.findById(tx.getDestinationWalletId())
                        .orElseThrow(() -> new WalletNotFoundException("Destination wallet not found"));

                if (wallet.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException("Insufficient funds for transfer");
                }

                wallet.setBalance(wallet.getBalance().subtract(amount));
                receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
                walletRepository.save(receiverWallet);
            }

            default -> throw new InvalidTransactionException("Unsupported transaction type");
        }

        walletRepository.save(wallet);
        tx.setStatus(TransactionStatusEnum.COMPLETED);
        transactionRepository.save(tx);
    }

    @Transactional
    public void processTransactionDecision(Long transactionId, boolean confirm) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new WalletNotFoundException("Transaction ID " + transactionId + " not found"));

        if (tx.getStatus() != TransactionStatusEnum.PENDING) {
            throw new InvalidTransactionException("Transaction already confirmed or cancelled");
        }

        if (!confirm) {
            tx.setStatus(TransactionStatusEnum.CANCELLED);
            transactionRepository.save(tx);
            return;
        }

        Wallet senderWallet = tx.getWallet();

        Wallet recipientWallet = walletRepository.findByAppUserIdAndWalletName(
                tx.getWallet().getAppUser().getId(),
                tx.getWallet().getWalletName()
        ).orElseThrow(() -> new WalletNotFoundException("Recipient wallet not found"));

        if (senderWallet.getBalance().compareTo(tx.getAmount().negate()) < 0) {
            throw new InsufficientFundsException("Insufficient funds at confirmation time");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(tx.getAmount().negate()));
        recipientWallet.setBalance(recipientWallet.getBalance().add(tx.getAmount().negate()));

        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);

        tx.setStatus(TransactionStatusEnum.CONFIRMED);
        transactionRepository.save(tx);
    }

    public List<TransactionDTO> getTransactionHistory(Long walletId, LocalDateTime from, LocalDateTime to) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        List<Transaction> transactions;

        if (from != null && to != null) {
            transactions = transactionRepository
                    .findByWalletIdAndTransactionDateBetweenOrderByTransactionDateDesc(walletId, from, to);
        } else if (from != null) {
            transactions = transactionRepository
                    .findByWalletIdAndTransactionDateAfterOrderByTransactionDateDesc(walletId, from);
        } else if (to != null) {
            transactions = transactionRepository
                    .findByWalletIdAndTransactionDateBeforeOrderByTransactionDateDesc(walletId, to);
        } else {
            transactions = transactionRepository.findByWalletIdOrderByTransactionDateDesc(walletId);
        }

        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

}
