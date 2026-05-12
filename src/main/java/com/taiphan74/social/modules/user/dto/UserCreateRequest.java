package com.taiphan74.social.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank
    @Size(max = 50)
    private String username;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;
}
