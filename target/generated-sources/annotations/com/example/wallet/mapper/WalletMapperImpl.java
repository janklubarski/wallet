package com.example.wallet.mapper;

import com.example.wallet.dto.WalletDTO;
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
public class WalletMapperImpl implements WalletMapper {

    @Override
    public WalletDTO toDTO(Wallet wallet) {
        if ( wallet == null ) {
            return null;
        }

        WalletDTO.WalletDTOBuilder walletDTO = WalletDTO.builder();

        walletDTO.id( wallet.getId() );
        walletDTO.walletName( wallet.getWalletName() );
        walletDTO.balance( wallet.getBalance() );
        walletDTO.currency( wallet.getCurrency() );

        return walletDTO.build();
    }

    @Override
    public List<WalletDTO> toDTOList(List<Wallet> wallets) {
        if ( wallets == null ) {
            return null;
        }

        List<WalletDTO> list = new ArrayList<WalletDTO>( wallets.size() );
        for ( Wallet wallet : wallets ) {
            list.add( toDTO( wallet ) );
        }

        return list;
    }
}
