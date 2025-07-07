package com.example.wallet.mapper;

import com.example.wallet.dto.WalletDTO;
import com.example.wallet.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    WalletDTO toDTO(Wallet wallet);

    List<WalletDTO> toDTOList(List<Wallet> wallets);

}
