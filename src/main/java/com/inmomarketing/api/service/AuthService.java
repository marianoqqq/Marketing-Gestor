package com.inmomarketing.api.service;

import com.inmomarketing.api.dto.request.AuthRequest;
import com.inmomarketing.api.dto.request.RegisterRequest;
import com.inmomarketing.api.dto.response.AuthResponse;
import com.inmomarketing.api.enums.UserStatus;
import com.inmomarketing.api.model.UserEntity;
import com.inmomarketing.api.repository.UserRepository;
import com.inmomarketing.api.securityJwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.inmomarketing.api.enums.UserRole;
import com.inmomarketing.api.model.InmobiliariaEntity;
import com.inmomarketing.api.repository.InmobiliariaRepository;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final InmobiliariaRepository inmobiliariaRepository;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findUserEntityByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        InmobiliariaEntity inmobiliaria = new InmobiliariaEntity();
        inmobiliaria.setNombre(request.nombreInmobiliaria());
        inmobiliariaRepository.save(inmobiliaria);

        UserEntity user = UserEntity.builder()
                .nombre(request.nombre())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .inmobiliaria(inmobiliaria)
                .build();

        userRepository.save(user);
    }
    public UserDetails authenticate(AuthRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findUserEntityByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + input.getEmail()));
    }

    @Transactional
    public AuthResponse refreshAccessToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);

        UserEntity user = userRepository.findUserEntityByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token does not match");
        }

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Refresh token expired or invalid");
        }

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

}