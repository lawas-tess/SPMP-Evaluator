package com.team02.spmpevaluator.security;

import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.RedirectStrategy;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OAuth2LoginSuccessHandler.
 * Tests OAuth2 authentication success handling and JWT token generation.
 */
@ExtendWith(MockitoExtension.class)
class OAuth2LoginSuccessHandlerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2User oAuth2User;

    @Mock
    private RedirectStrategy redirectStrategy;

    @InjectMocks
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("oauth.user@gmail.com");
        testUser.setEmail("oauth.user@gmail.com");
        testUser.setFirstName("OAuth");
        testUser.setLastName("User");
        testUser.setRole(Role.STUDENT);
        testUser.setEnabled(true);

        // Set up redirect strategy
        oAuth2LoginSuccessHandler.setRedirectStrategy(redirectStrategy);
    }

    @Nested
    @DisplayName("Authentication Success Tests")
    class AuthenticationSuccessTests {

        @Test
        @DisplayName("Should extract email from OAuth2 user")
        void onAuthenticationSuccess_ExtractsEmail() throws Exception {
            when(authentication.getPrincipal()).thenReturn(oAuth2User);
            when(oAuth2User.getAttribute("email")).thenReturn("oauth.user@gmail.com");
            when(oAuth2User.getAttribute("name")).thenReturn("OAuth User");
            when(userService.processOAuthPostLogin("oauth.user@gmail.com", "OAuth User"))
                    .thenReturn(testUser);
            when(jwtUtil.generateToken(anyString())).thenReturn("generated.jwt.token");

            oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

            verify(oAuth2User).getAttribute("email");
        }

        @Test
        @DisplayName("Should extract name from OAuth2 user")
        void onAuthenticationSuccess_ExtractsName() throws Exception {
            when(authentication.getPrincipal()).thenReturn(oAuth2User);
            when(oAuth2User.getAttribute("email")).thenReturn("oauth.user@gmail.com");
            when(oAuth2User.getAttribute("name")).thenReturn("OAuth User");
            when(userService.processOAuthPostLogin(anyString(), anyString())).thenReturn(testUser);
            when(jwtUtil.generateToken(anyString())).thenReturn("generated.jwt.token");

            oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

            verify(oAuth2User).getAttribute("name");
        }

        @Test
        @DisplayName("Should call processOAuthPostLogin with email and name")
        void onAuthenticationSuccess_CallsProcessOAuthPostLogin() throws Exception {
            when(authentication.getPrincipal()).thenReturn(oAuth2User);
            when(oAuth2User.getAttribute("email")).thenReturn("oauth.user@gmail.com");
            when(oAuth2User.getAttribute("name")).thenReturn("OAuth User");
            when(userService.processOAuthPostLogin("oauth.user@gmail.com", "OAuth User"))
                    .thenReturn(testUser);
            when(jwtUtil.generateToken(anyString())).thenReturn("generated.jwt.token");

            oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

            verify(userService).processOAuthPostLogin("oauth.user@gmail.com", "OAuth User");
        }

        @Test
        @DisplayName("Should generate JWT token for user")
        void onAuthenticationSuccess_GeneratesJwtToken() throws Exception {
            when(authentication.getPrincipal()).thenReturn(oAuth2User);
            when(oAuth2User.getAttribute("email")).thenReturn("oauth.user@gmail.com");
            when(oAuth2User.getAttribute("name")).thenReturn("OAuth User");
            when(userService.processOAuthPostLogin(anyString(), anyString())).thenReturn(testUser);
            when(jwtUtil.generateToken("oauth.user@gmail.com")).thenReturn("generated.jwt.token");

            oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

            verify(jwtUtil).generateToken("oauth.user@gmail.com");
        }
    }

    @Nested
    @DisplayName("Redirect Tests")
    class RedirectTests {

        @Test
        @DisplayName("Should redirect to frontend callback URL with token")
        void onAuthenticationSuccess_RedirectsToCallback() throws Exception {
            when(authentication.getPrincipal()).thenReturn(oAuth2User);
            when(oAuth2User.getAttribute("email")).thenReturn("oauth.user@gmail.com");
            when(oAuth2User.getAttribute("name")).thenReturn("OAuth User");
            when(userService.processOAuthPostLogin(anyString(), anyString())).thenReturn(testUser);
            when(jwtUtil.generateToken(anyString())).thenReturn("generated.jwt.token");

            oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

            ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
            verify(redirectStrategy).sendRedirect(eq(request), eq(response), urlCaptor.capture());

            String redirectUrl = urlCaptor.getValue();
            assertTrue(redirectUrl.contains("localhost:3000"));
            assertTrue(redirectUrl.contains("/auth/google/callback"));
            assertTrue(redirectUrl.contains("token=generated.jwt.token"));
        }

        @Test
        @DisplayName("Should include token as query parameter")
        void onAuthenticationSuccess_IncludesTokenInUrl() throws Exception {
            when(authentication.getPrincipal()).thenReturn(oAuth2User);
            when(oAuth2User.getAttribute("email")).thenReturn("oauth.user@gmail.com");
            when(oAuth2User.getAttribute("name")).thenReturn("OAuth User");
            when(userService.processOAuthPostLogin(anyString(), anyString())).thenReturn(testUser);
            when(jwtUtil.generateToken(anyString())).thenReturn("my.jwt.token.here");

            oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

            ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
            verify(redirectStrategy).sendRedirect(any(), any(), urlCaptor.capture());

            assertTrue(urlCaptor.getValue().contains("token=my.jwt.token.here"));
        }
    }

    @Nested
    @DisplayName("User Processing Tests")
    class UserProcessingTests {

        @Test
        @DisplayName("Should use user's username for token generation")
        void onAuthenticationSuccess_UsesUsernameForToken() throws Exception {
            testUser.setUsername("custom.username");
            when(authentication.getPrincipal()).thenReturn(oAuth2User);
            when(oAuth2User.getAttribute("email")).thenReturn("oauth.user@gmail.com");
            when(oAuth2User.getAttribute("name")).thenReturn("OAuth User");
            when(userService.processOAuthPostLogin(anyString(), anyString())).thenReturn(testUser);
            when(jwtUtil.generateToken("custom.username")).thenReturn("generated.jwt.token");

            oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

            verify(jwtUtil).generateToken("custom.username");
        }

        @Test
        @DisplayName("Should handle new user creation")
        void onAuthenticationSuccess_HandlesNewUser() throws Exception {
            User newUser = new User();
            newUser.setId(2L);
            newUser.setUsername("newuser@gmail.com");
            newUser.setEmail("newuser@gmail.com");

            when(authentication.getPrincipal()).thenReturn(oAuth2User);
            when(oAuth2User.getAttribute("email")).thenReturn("newuser@gmail.com");
            when(oAuth2User.getAttribute("name")).thenReturn("New User");
            when(userService.processOAuthPostLogin("newuser@gmail.com", "New User"))
                    .thenReturn(newUser);
            when(jwtUtil.generateToken("newuser@gmail.com")).thenReturn("new.user.token");

            oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

            verify(userService).processOAuthPostLogin("newuser@gmail.com", "New User");
            verify(jwtUtil).generateToken("newuser@gmail.com");
        }
    }

    @Nested
    @DisplayName("Null Attribute Tests")
    class NullAttributeTests {

        @Test
        @DisplayName("Should handle null email from OAuth2 user")
        void onAuthenticationSuccess_NullEmail_PassesToService() throws Exception {
            when(authentication.getPrincipal()).thenReturn(oAuth2User);
            when(oAuth2User.getAttribute("email")).thenReturn(null);
            when(oAuth2User.getAttribute("name")).thenReturn("User Name");
            when(userService.processOAuthPostLogin(null, "User Name")).thenReturn(testUser);
            when(jwtUtil.generateToken(anyString())).thenReturn("token");

            oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

            verify(userService).processOAuthPostLogin(null, "User Name");
        }

        @Test
        @DisplayName("Should handle null name from OAuth2 user")
        void onAuthenticationSuccess_NullName_PassesToService() throws Exception {
            when(authentication.getPrincipal()).thenReturn(oAuth2User);
            when(oAuth2User.getAttribute("email")).thenReturn("user@gmail.com");
            when(oAuth2User.getAttribute("name")).thenReturn(null);
            when(userService.processOAuthPostLogin("user@gmail.com", null)).thenReturn(testUser);
            when(jwtUtil.generateToken(anyString())).thenReturn("token");

            oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

            verify(userService).processOAuthPostLogin("user@gmail.com", null);
        }
    }
}
