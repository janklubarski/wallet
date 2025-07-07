package com.example.wallet.conf;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AppStartupTime {

    private final Instant startedAt = Instant.now();

    public Instant getStartedAt() {
        return startedAt;
    }

}
