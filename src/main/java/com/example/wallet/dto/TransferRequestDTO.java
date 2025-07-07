package com.example.wallet.dto;

import com.example.wallet.entity.enums.CurrencyEnum;
import com.example.wallet.entity.enums.TransactionTypeEnum;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequestDTO {

    private Long walletId;               // sender
    private Long receiverWalletId;       // optional, for internal transfer
    private String bankAccountNumber;    // optional, for withdrawal
    private BigDecimal amount;
    private CurrencyEnum currency;
    private TransactionTypeEnum transactionType;
    private String variableSymbol;
    private String description;

}
