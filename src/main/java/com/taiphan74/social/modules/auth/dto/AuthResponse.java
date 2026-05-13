package com.taiphan74.social.modules.auth.dto;

import com.taiphan74.social.modules.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserResponse user;
}
