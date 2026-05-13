package com.taiphan74.social.modules.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User role", enumAsRef = true)
public enum ERole {
    USER, ADMIN
}
