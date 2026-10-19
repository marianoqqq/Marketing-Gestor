package com.inmomarketing.api.securityJwt;

import org.springframework.security.core.userdetails.UserDetails;
public interface JwtService {

    String extractUsername(String token);

    String generateToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean validateRefreshToken(String refreshToken, UserDetails userDetails);
}
