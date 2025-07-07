package com.example.wallet.entity;

import com.example.wallet.entity.enums.CurrencyEnum;
import com.example.wallet.entity.enums.TransactionStatusEnum;
import com.example.wallet.entity.enums.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACTION_LOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRANSACTION_SEQ")
    @SequenceGenerator(name = "TRANSACTION_SEQ", sequenceName = "TRANSACTION_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WALLET_ID", nullable = false)
    private Wallet wallet;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency; // EUR or CZK

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;

    @Column
    private String description;

    @Column
    private String variableSymbol;

    @CreationTimestamp
    private LocalDateTime transactionDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatusEnum status;

    @Column
    private String bankAccountNumber;

    @Column
    private Long destinationWalletId;

}