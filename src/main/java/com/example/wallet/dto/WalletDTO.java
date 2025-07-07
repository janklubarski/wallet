// package com.example.wallet.dto

package com.example.wallet.dto;

import com.example.wallet.entity.enums.CurrencyEnum;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletDTO {

    private Long id;
    private String walletName;
    private BigDecimal balance;
    private CurrencyEnum currency;

}
