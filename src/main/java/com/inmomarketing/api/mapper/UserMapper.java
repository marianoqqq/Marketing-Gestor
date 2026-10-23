package com.inmomarketing.api.mapper;

import com.inmomarketing.api.dto.response.UserResponse;
import com.inmomarketing.api.model.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserResponse Userresponse = new UserResponse();
        Userresponse.setId(entity.getId());
        Userresponse.setEmail(entity.getEmail());
        Userresponse.setRole(entity.getRole());
        Userresponse.setStatus(entity.getStatus());
        Userresponse.setCreatedAt(entity.getCreatedAt());
        return Userresponse;
    }
}