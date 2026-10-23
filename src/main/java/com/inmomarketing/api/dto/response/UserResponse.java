package com.inmomarketing.api.dto.response;

import com.inmomarketing.api.enums.UserRole;
import com.inmomarketing.api.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
}