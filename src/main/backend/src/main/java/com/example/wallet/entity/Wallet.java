package com.example.wallet.entity;

import com.example.wallet.entity.enums.CurrencyEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "WALLET", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"USER_ID", "WALLET_NAME"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WALLET_SEQ")
    @SequenceGenerator(name = "WALLET_SEQ", sequenceName = "WALLET_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser appUser;

    @Column(nullable = false, length = 100)
    private String walletName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency; // EUR or CZK

}