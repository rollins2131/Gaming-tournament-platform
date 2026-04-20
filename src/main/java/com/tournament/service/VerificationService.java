package com.tournament.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationService {

    private final Map<Integer, String> emailTokens = new ConcurrentHashMap<>();

    // SRP: verification logic isolated from authentication.
    public String issueEmailToken(Integer userId) {
        String token = UUID.randomUUID().toString();
        emailTokens.put(userId, token);
        return token;
    }

    public boolean verifyEmailToken(Integer userId, String token) {
        String stored = emailTokens.get(userId);
        return stored != null && stored.equals(token);
    }
}
