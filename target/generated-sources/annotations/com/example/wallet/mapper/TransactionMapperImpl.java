package com.example.wallet.mapper;

import com.example.wallet.dto.TransactionDTO;
import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.Wallet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-07T08:12:51+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public TransactionDTO toDTO(Transaction transaction) {
        if ( transaction == null ) {
            return null;
        }

        TransactionDTO.TransactionDTOBuilder transactionDTO = TransactionDTO.builder();

        transactionDTO.walletId( transactionWalletId( transaction ) );
        transactionDTO.id( transaction.getId() );
        transactionDTO.amount( transaction.getAmount() );
        transactionDTO.currency( transaction.getCurrency() );
        transactionDTO.transactionType( transaction.getTransactionType() );
        transactionDTO.description( transaction.getDescription() );
        transactionDTO.variableSymbol( transaction.getVariableSymbol() );
        transactionDTO.transactionDate( transaction.getTransactionDate() );
        if ( transaction.getStatus() != null ) {
            transactionDTO.status( transaction.getStatus().name() );
        }

        return transactionDTO.build();
    }

    @Override
    public List<TransactionDTO> toDTOList(List<Transaction> transactions) {
        if ( transactions == null ) {
            return null;
        }

        List<TransactionDTO> list = new ArrayList<TransactionDTO>( transactions.size() );
        for ( Transaction transaction : transactions ) {
            list.add( toDTO( transaction ) );
        }

        return list;
    }

    private Long transactionWalletId(Transaction transaction) {
        Wallet wallet = transaction.getWallet();
        if ( wallet == null ) {
            return null;
        }
        return wallet.getId();
    }
}
