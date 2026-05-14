package com.taiphan74.social.modules.profile.service;

import com.taiphan74.social.exception.NotFoundException;
import com.taiphan74.social.modules.profile.dto.ProfileResponse;
import com.taiphan74.social.modules.profile.dto.ProfileUpdateRequest;
import com.taiphan74.social.modules.profile.entity.UserProfile;
import com.taiphan74.social.modules.profile.mapper.ProfileMapper;
import com.taiphan74.social.modules.profile.repository.UserProfileRepository;
import com.taiphan74.social.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements IProfileService {

    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    @Override
    public ProfileResponse getByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        return profileMapper.toResponse(profile);
    }

    @Transactional
    @Override
    public ProfileResponse upsert(UUID userId, ProfileUpdateRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> UserProfile.builder().userId(userId).build());

        if (request.getDisplayName() != null) {
            profile.setDisplayName(request.getDisplayName());
        }
        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }

        return profileMapper.toResponse(profileRepository.save(profile));
    }
}
