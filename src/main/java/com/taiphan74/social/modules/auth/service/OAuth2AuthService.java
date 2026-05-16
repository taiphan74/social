package com.taiphan74.social.modules.auth.service;

import com.taiphan74.social.modules.user.entity.ERole;
import com.taiphan74.social.modules.user.entity.User;
import com.taiphan74.social.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2AuthService {

    private static final int USERNAME_MAX_LENGTH = 50;
    private static final String GOOGLE_USERNAME_PREFIX = "gg_";
    private static final String DEFAULT_GOOGLE_USERNAME = "gg_user";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User findOrCreateGoogleUser(String email, String name) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Google account email is missing");
        }

        return userRepository.findByEmail(email)
                .map(this::verifyExistingUser)
                .orElseGet(() -> createGoogleUser(email, name));
    }

    private User verifyExistingUser(User user) {
        if (user.isVerified()) {
            return user;
        }

        user.setVerified(true);
        return userRepository.save(user);
    }

    private User createGoogleUser(String email, String name) {
        String username = generateUniqueGoogleUsername(email, name);
        String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        User user = User.builder()
                .username(username)
                .password(randomPassword)
                .email(email)
                .enabled(true)
                .accountNonLocked(true)
                .verified(true)
                .usernameCustomized(false)
                .role(ERole.USER)
                .build();

        return userRepository.save(user);
    }

    private String generateUniqueGoogleUsername(String email, String name) {
        String rawBase = extractBaseUsername(email, name);
        String base = GOOGLE_USERNAME_PREFIX + sanitizeUsername(rawBase);

        if (base.equals(GOOGLE_USERNAME_PREFIX)) {
            base = DEFAULT_GOOGLE_USERNAME;
        }

        base = trimToMaxLength(base, USERNAME_MAX_LENGTH);

        String candidate = base;
        int counter = 1;

        while (userRepository.existsByUsername(candidate)) {
            String suffix = "_" + counter++;
            candidate = trimToMaxLength(base, USERNAME_MAX_LENGTH - suffix.length()) + suffix;
        }

        return candidate;
    }

    private String extractBaseUsername(String email, String name) {
        if (name != null && !name.isBlank()) {
            return name;
        }

        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            return email.substring(0, atIndex);
        }

        return email;
    }

    private String sanitizeUsername(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return normalized
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_+", "")
                .replaceAll("_+$", "");
    }

    private String trimToMaxLength(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }
}
