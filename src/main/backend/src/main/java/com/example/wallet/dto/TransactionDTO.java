package com.example.wallet.dto;

import com.example.wallet.entity.enums.CurrencyEnum;
import com.example.wallet.entity.enums.TransactionTypeEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {

    private Long id;
    private Long walletId;
    private BigDecimal amount;
    private CurrencyEnum currency;
    private TransactionTypeEnum transactionType;
    private String description;
    private String variableSymbol;
    private LocalDateTime transactionDate;
    private String status;

}
