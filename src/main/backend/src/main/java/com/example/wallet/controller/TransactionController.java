package com.example.wallet.controller;

import com.example.wallet.dto.TransactionDTO;
import com.example.wallet.dto.TransferRequestDTO;
import com.example.wallet.service.QRService;
import com.example.wallet.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final QRService qrService;

    @PostMapping("/create")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransferRequestDTO dto) {
        TransactionDTO pending = transactionService.createPendingTransaction(dto);
        return ResponseEntity.ok(pending);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmTransaction(
            @RequestParam Long transactionId,
            @RequestParam boolean confirm
    ) {
        transactionService.processTransactionDecision(transactionId, confirm);
        return ResponseEntity.ok(confirm ? "Transaction confirmed" : "Transaction cancelled");
    }

    // not used at this stage
    @GetMapping("/qr/{transactionId}")
    public ResponseEntity<String> generateQRCode(@PathVariable Long transactionId) throws Exception {
        String content = "wallet://confirm?transactionId=" + transactionId;
        String base64Image = qrService.generateQRBase64(content, 300, 300);
        return ResponseEntity.ok(base64Image); // frontend can render this in an <img src="data:image/png;base64,..."/>
    }

    @GetMapping("/history/{walletId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionHistory(
            @PathVariable Long walletId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {

        List<TransactionDTO> history = transactionService.getTransactionHistory(walletId, dateFrom, dateTo);
        return ResponseEntity.ok(history);
    }

}
