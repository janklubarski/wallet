package com.example.wallet.mapper;

import com.example.wallet.dto.exception.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import javax.annotation.processing.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-07T14:30:25+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ErrorResponseMapperImpl implements ErrorResponseMapper {

    @Override
    public ErrorResponseDTO toDTO(String message, HttpStatus status, HttpServletRequest request) {
        if ( message == null && status == null && request == null ) {
            return null;
        }

        ErrorResponseDTO.ErrorResponseDTOBuilder errorResponseDTO = ErrorResponseDTO.builder();

        if ( status != null ) {
            errorResponseDTO.status( mapStatusToCode( status ) );
            errorResponseDTO.error( mapStatusToReason( status ) );
        }
        errorResponseDTO.message( message );
        errorResponseDTO.timestamp( java.time.LocalDateTime.now() );
        errorResponseDTO.path( request.getRequestURI() );

        return errorResponseDTO.build();
    }
}
