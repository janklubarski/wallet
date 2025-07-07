package com.example.wallet.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequestDTO {

    private String username;
    private String password;

}
