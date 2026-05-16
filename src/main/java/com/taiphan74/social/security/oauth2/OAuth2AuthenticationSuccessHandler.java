package com.taiphan74.social.security.oauth2;

import com.taiphan74.social.modules.auth.service.OAuth2AuthService;
import com.taiphan74.social.modules.auth.service.RefreshTokenService;
import com.taiphan74.social.modules.user.entity.User;
import com.taiphan74.social.modules.user.service.CustomUserDetailsService;
import com.taiphan74.social.security.jwt.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String REFRESH_COOKIE = "refreshToken";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;

    private final OAuth2AuthService oAuth2AuthService;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String authorizedRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = oAuth2AuthService.findOrCreateGoogleUser(email, name);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtUtils.generateToken(userDetails);

        issueRefreshToken(response, user.getUsername(), user.getId().toString());

        String targetUrl = UriComponentsBuilder
                .fromUriString(authorizedRedirectUri)
                .queryParam("accessToken", accessToken)
                .build()
                .toUriString();

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void issueRefreshToken(HttpServletResponse response, String username, String userId) {
        String familyId = UUID.randomUUID().toString();
        String refreshToken = jwtUtils.generateRefreshToken(username, familyId);
        String tokenId = jwtUtils.extractTokenId(refreshToken);

        refreshTokenService.save(userId, familyId, tokenId);
        response.addCookie(buildCookie(refreshToken, COOKIE_MAX_AGE));
    }

    private Cookie buildCookie(String value, int maxAge) {
        Cookie cookie = new Cookie(REFRESH_COOKIE, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }
}
