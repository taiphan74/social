package com.taiphan74.social.modules.user.service;

import com.taiphan74.social.exception.ConflictException;
import com.taiphan74.social.exception.NotFoundException;
import com.taiphan74.social.modules.user.dto.UserCreateRequest;
import com.taiphan74.social.modules.user.dto.UserResponse;
import com.taiphan74.social.modules.user.entity.ERole;
import com.taiphan74.social.modules.user.entity.User;
import com.taiphan74.social.modules.user.mapper.UserMapper;
import com.taiphan74.social.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(ERole.USER)
                .enabled(true)
                .accountNonLocked(true)
                .verified(false)
                .build();
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public Page<UserResponse> getAll(Pageable pageable) {
        return userMapper.toResponsePage(userRepository.findAll(pageable));
    }

    @Override
    public UserResponse getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void setVerified(UUID id, boolean verified) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User không tồn tại"));
        user.setVerified(verified);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updatePassword(UUID id, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User không tồn tại"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
