package com.taiphan74.social.modules.profile.service;

import com.taiphan74.social.modules.profile.dto.ProfileResponse;
import com.taiphan74.social.modules.profile.dto.ProfileUpdateRequest;

import java.util.UUID;

public interface IProfileService {
    ProfileResponse getByUserId(UUID userId);
    ProfileResponse upsert(UUID userId, ProfileUpdateRequest request);
}
