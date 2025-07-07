package com.example.wallet.mapper;

import com.example.wallet.dto.exception.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;

@Mapper(componentModel = "spring")
public interface ErrorResponseMapper {

    ErrorResponseMapper INSTANCE = Mappers.getMapper(ErrorResponseMapper.class);

    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", source = "status", qualifiedByName = "httpStatusToCode")
    @Mapping(target = "error", source = "status", qualifiedByName = "httpStatusToReason")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "path", expression = "java(request.getRequestURI())")
    ErrorResponseDTO toDTO(String message, HttpStatus status, HttpServletRequest request);

    @Named("httpStatusToCode")
    default int mapStatusToCode(HttpStatus status) {
        return status.value();
    }

    @Named("httpStatusToReason")
    default String mapStatusToReason(HttpStatus status) {
        return status.getReasonPhrase();
    }

}
