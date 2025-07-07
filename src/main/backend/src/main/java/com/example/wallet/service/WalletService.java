package com.example.wallet.service;

import com.example.wallet.dto.TransactionDTO;
import com.example.wallet.dto.WalletDTO;
import com.example.wallet.entity.AppUser;
import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.Wallet;
import com.example.wallet.entity.enums.CurrencyEnum;
import com.example.wallet.entity.enums.TransactionStatusEnum;
import com.example.wallet.entity.enums.TransactionTypeEnum;
import com.example.wallet.exception.InsufficientFundsException;
import com.example.wallet.exception.InvalidTransactionException;
import com.example.wallet.exception.WalletCreationException;
import com.example.wallet.exception.WalletNotFoundException;
import com.example.wallet.mapper.TransactionMapper;
import com.example.wallet.mapper.WalletMapper;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;

    public List<WalletDTO> getUserWallets(String username) {
        AppUser appUser = getUserByUsername(username);
        List<Wallet> wallets = walletRepository.findByAppUserId(appUser.getId());
        return walletMapper.toDTOList(wallets);
    }

    public WalletDTO getWallet(Long walletId, String username) {
        return walletMapper.toDTO(getWalletInner(walletId, username));
    }

    private Wallet getWalletInner(Long walletId, String username) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        if (!wallet.getAppUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access to wallet");
        }
        return wallet;
    }

    @Transactional
    public WalletDTO createWallet(String username, CurrencyEnum currency, String walletName) {
        AppUser appUser = getUserByUsername(username);

        walletRepository.findByAppUserIdAndWalletName(appUser.getId(), currency.name())
                .ifPresent(w -> {
                    throw new WalletCreationException("Wallet with this name already exists");
                });

        Wallet wallet = Wallet.builder()
                .appUser(appUser)
                .currency(currency)
                .walletName(walletName)
                .balance(BigDecimal.ZERO)
                .build();

        walletRepository.save(wallet);
        return walletMapper.toDTO(wallet);
    }

    @Transactional
    public TransactionDTO transfer(Long fromWalletId, Long toWalletId, BigDecimal amount, String username, String description, String variableSymbol) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        Wallet fromWallet = getWalletInner(fromWalletId, username);
        Wallet toWallet = walletRepository.findById(toWalletId)
                .orElseThrow(() -> new WalletNotFoundException("Target wallet not found"));

        if (!fromWallet.getCurrency().equals(toWallet.getCurrency())) {
            throw new InvalidTransactionException("Cannot transfer between wallets of different currencies");
        }

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in sender wallet");
        }

        // Perform balance updates
        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
        toWallet.setBalance(toWallet.getBalance().add(amount));
        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        LocalDateTime now = LocalDateTime.now();

        // Create the outgoing transaction (TRANSFER - sender)
        Transaction outgoing = Transaction.builder()
                .wallet(fromWallet)
                .amount(amount.negate())
                .currency(fromWallet.getCurrency())
                .transactionType(TransactionTypeEnum.TRANSFER)
                .description(description)
                .variableSymbol(variableSymbol)
                .transactionDate(now)
                .status(TransactionStatusEnum.COMPLETED)
                .destinationWalletId(toWallet.getId())
                .build();

        // Create the incoming transaction (INCOME - recipient)
        Transaction incoming = Transaction.builder()
                .wallet(toWallet)
                .amount(amount)
                .currency(toWallet.getCurrency())
                .transactionType(TransactionTypeEnum.INCOME)
                .description(description)
                .variableSymbol(variableSymbol)
                .transactionDate(now)
                .status(TransactionStatusEnum.COMPLETED)
                .build();

        transactionRepository.save(outgoing);
        transactionRepository.save(incoming);
        return transactionMapper.toDTO(incoming);
    }

    @Transactional
    public TransactionDTO withdraw(Long walletId, String bankAccountNumber, BigDecimal amount, String username, String description, String variableSymbol) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be greater than zero");
        }

        Wallet wallet = getWalletInner(walletId, username);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in wallet");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(amount.negate())
                .currency(wallet.getCurrency())
                .transactionType(TransactionTypeEnum.WITHDRAWAL)
                .description(description)
                .variableSymbol(variableSymbol)
                .bankAccountNumber(bankAccountNumber)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatusEnum.COMPLETED)
                .build();

        transactionRepository.save(transaction);
        return transactionMapper.toDTO(transaction);
    }

    @Transactional
    public TransactionDTO loadFunds(Long walletId, BigDecimal amount, String username, String description, String variableSymbol) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be greater than zero");
        }

        Wallet wallet = getWalletInner(walletId, username);

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .currency(wallet.getCurrency())
                .transactionType(TransactionTypeEnum.INCOME)
                .description(description)
                .variableSymbol(variableSymbol)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatusEnum.COMPLETED)
                .build();

        transactionRepository.save(transaction);
        return transactionMapper.toDTO(transaction);
    }

    private AppUser getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("AppUser not found"));
    }

}
