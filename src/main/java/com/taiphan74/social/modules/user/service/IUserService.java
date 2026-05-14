package com.taiphan74.social.modules.user.service;

import com.taiphan74.social.modules.user.dto.UserCreateRequest;
import com.taiphan74.social.modules.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface IUserService {
    UserResponse create(UserCreateRequest request);
    Page<UserResponse> getAll(Pageable pageable);
    UserResponse getById(UUID id);
    UserResponse findByUsername(String username);
    UserResponse findByEmail(String email);
    void delete(UUID id);
    void setVerified(UUID id, boolean verified);
    void updatePassword(UUID id, String newPassword);
}
