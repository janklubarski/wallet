package com.example.wallet.controller;

import com.example.wallet.dto.TransactionDTO;
import com.example.wallet.dto.TransferRequestDTO;
import com.example.wallet.dto.WalletCreateRequestDTO;
import com.example.wallet.dto.WalletDTO;
import com.example.wallet.entity.enums.TransactionTypeEnum;
import com.example.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<List<WalletDTO>> getUserWallets(Principal principal) {
        return ResponseEntity.ok(walletService.getUserWallets(principal.getName()));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletDTO> getWallet(@PathVariable Long walletId, Principal principal) {
        return ResponseEntity.ok(walletService.getWallet(walletId, principal.getName()));
    }

    @PostMapping
    public ResponseEntity<WalletDTO> createWallet(@Valid @RequestBody WalletCreateRequestDTO request, Principal principal) {
        return ResponseEntity.ok(walletService.createWallet(
                principal.getName(),
                request.getCurrency(),
                request.getName()
        ));
    }

    @PostMapping("/operation")
    public ResponseEntity<TransactionDTO> handleWalletOperation(@RequestBody TransferRequestDTO request, Principal principal) {
        String username = principal.getName();
        TransactionTypeEnum type = request.getTransactionType();

        return switch (type) {
            case TRANSFER -> ResponseEntity.ok(walletService.transfer(
                    request.getWalletId(),
                    request.getReceiverWalletId(),
                    request.getAmount(),
                    username,
                    request.getDescription(),
                    request.getVariableSymbol()
            ));
            case WITHDRAWAL -> ResponseEntity.ok(walletService.withdraw(
                    request.getWalletId(),
                    request.getBankAccountNumber(),
                    request.getAmount(),
                    username,
                    request.getDescription(),
                    request.getVariableSymbol()
            ));
            case INCOME -> ResponseEntity.ok(walletService.loadFunds(
                    request.getWalletId(),
                    request.getAmount(),
                    username,
                    request.getDescription(),
                    request.getVariableSymbol()
            ));
        };
    }

}