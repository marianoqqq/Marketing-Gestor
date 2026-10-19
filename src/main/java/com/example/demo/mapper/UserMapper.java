package com.example.demo.mapper;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.model.UserEntity;
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
        Userresponse.setCreatedAt(entity.getCreatedAt());
        return Userresponse;
    }

}