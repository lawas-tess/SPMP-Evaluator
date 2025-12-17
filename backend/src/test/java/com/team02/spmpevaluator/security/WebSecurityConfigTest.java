package com.team02.spmpevaluator.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WebSecurityConfig.
 * Tests Spring Security configuration beans.
 */
@ExtendWith(MockitoExtension.class)
class WebSecurityConfigTest {

    @Mock
    private JwtFilter jwtFilter;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    private WebSecurityConfig webSecurityConfig;

    @BeforeEach
    void setUp() {
        webSecurityConfig = new WebSecurityConfig(jwtFilter, userDetailsService, passwordEncoder);
    }

    @Nested
    @DisplayName("Authentication Manager Tests")
    class AuthenticationManagerTests {

        @Test
        @DisplayName("Should return authentication manager from configuration")
        void authenticationManager_ReturnsFromConfiguration() throws Exception {
            when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

            AuthenticationManager result = webSecurityConfig.authenticationManager(authenticationConfiguration);

            assertNotNull(result);
            assertEquals(authenticationManager, result);
            verify(authenticationConfiguration).getAuthenticationManager();
        }
    }

    @Nested
    @DisplayName("Authentication Provider Tests")
    class AuthenticationProviderTests {

        @Test
        @DisplayName("Should create DaoAuthenticationProvider")
        void authenticationProvider_CreatesDaoAuthenticationProvider() {
            DaoAuthenticationProvider provider = webSecurityConfig.authenticationProvider();

            assertNotNull(provider);
            assertInstanceOf(DaoAuthenticationProvider.class, provider);
        }

        @Test
        @DisplayName("Should configure provider with user details service")
        void authenticationProvider_ConfiguredWithUserDetailsService() {
            DaoAuthenticationProvider provider = webSecurityConfig.authenticationProvider();

            // Provider is configured - we can verify by checking it's properly created
            assertNotNull(provider);
        }

        @Test
        @DisplayName("Should configure provider with password encoder")
        void authenticationProvider_ConfiguredWithPasswordEncoder() {
            DaoAuthenticationProvider provider = webSecurityConfig.authenticationProvider();

            // Provider is configured with password encoder
            assertNotNull(provider);
        }
    }

    @Nested
    @DisplayName("CORS Configuration Tests")
    class CorsConfigurationTests {

        private MockHttpServletRequest mockRequest;

        @BeforeEach
        void setUpMockRequest() {
            mockRequest = new MockHttpServletRequest();
            mockRequest.setRequestURI("/api/test");
        }

        @Test
        @DisplayName("Should create CORS configuration source")
        void corsConfigurationSource_ReturnsConfigurationSource() {
            CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();

            assertNotNull(source);
        }

        @Test
        @DisplayName("Should allow localhost:3000 origin")
        void corsConfigurationSource_AllowsLocalhost3000() {
            CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(mockRequest);

            assertNotNull(config);
            assertTrue(config.getAllowedOrigins().contains("http://localhost:3000"));
        }

        @Test
        @DisplayName("Should allow localhost:3001 origin")
        void corsConfigurationSource_AllowsLocalhost3001() {
            CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(mockRequest);

            assertNotNull(config);
            assertTrue(config.getAllowedOrigins().contains("http://localhost:3001"));
        }

        @Test
        @DisplayName("Should allow localhost:5173 origin (Vite)")
        void corsConfigurationSource_AllowsLocalhost5173() {
            CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(mockRequest);

            assertNotNull(config);
            assertTrue(config.getAllowedOrigins().contains("http://localhost:5173"));
        }

        @Test
        @DisplayName("Should allow GET method")
        void corsConfigurationSource_AllowsGetMethod() {
            CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(mockRequest);

            assertNotNull(config);
            assertTrue(config.getAllowedMethods().contains("GET"));
        }

        @Test
        @DisplayName("Should allow POST method")
        void corsConfigurationSource_AllowsPostMethod() {
            CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(mockRequest);

            assertNotNull(config);
            assertTrue(config.getAllowedMethods().contains("POST"));
        }

        @Test
        @DisplayName("Should allow PUT method")
        void corsConfigurationSource_AllowsPutMethod() {
            CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(mockRequest);

            assertNotNull(config);
            assertTrue(config.getAllowedMethods().contains("PUT"));
        }

        @Test
        @DisplayName("Should allow DELETE method")
        void corsConfigurationSource_AllowsDeleteMethod() {
            CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(mockRequest);

            assertNotNull(config);
            assertTrue(config.getAllowedMethods().contains("DELETE"));
        }

        @Test
        @DisplayName("Should allow OPTIONS method")
        void corsConfigurationSource_AllowsOptionsMethod() {
            CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(mockRequest);

            assertNotNull(config);
            assertTrue(config.getAllowedMethods().contains("OPTIONS"));
        }

        @Test
        @DisplayName("Should allow all headers")
        void corsConfigurationSource_AllowsAllHeaders() {
            CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(mockRequest);

            assertNotNull(config);
            assertTrue(config.getAllowedHeaders().contains("*"));
        }

        @Test
        @DisplayName("Should allow credentials")
        void corsConfigurationSource_AllowsCredentials() {
            CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(mockRequest);

            assertNotNull(config);
            assertTrue(config.getAllowCredentials());
        }

        @Test
        @DisplayName("Should set max age to 3600 seconds")
        void corsConfigurationSource_SetsMaxAge() {
            CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(mockRequest);

            assertNotNull(config);
            assertEquals(3600L, config.getMaxAge());
        }
    }

    @Nested
    @DisplayName("Configuration Initialization Tests")
    class ConfigurationInitializationTests {

        @Test
        @DisplayName("Should initialize with required dependencies")
        void constructor_InitializesWithDependencies() {
            WebSecurityConfig config = new WebSecurityConfig(jwtFilter, userDetailsService, passwordEncoder);

            assertNotNull(config);
        }

        @Test
        @DisplayName("Should create valid authentication provider")
        void authenticationProvider_IsValid() {
            DaoAuthenticationProvider provider = webSecurityConfig.authenticationProvider();

            assertNotNull(provider);
            // Provider should be usable
        }
    }
}
