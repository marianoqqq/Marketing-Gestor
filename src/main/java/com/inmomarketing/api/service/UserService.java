package com.inmomarketing.api.service;

import com.inmomarketing.api.dto.request.UserRequest;
import com.inmomarketing.api.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);
    UserResponse getById(Long id);
    List<UserResponse> getAll();
    UserResponse update(Long id, UserRequest request);
    void delete(Long id);
}
