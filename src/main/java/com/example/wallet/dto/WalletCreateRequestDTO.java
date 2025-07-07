package com.example.wallet.dto;

import com.example.wallet.entity.enums.CurrencyEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletCreateRequestDTO {

    @NotNull
    private CurrencyEnum currency;

    @NotNull
    @Size(min = 3, max = 100, message = "Wallet name must be between 3 and 100 characters.")
    private String name;

}
