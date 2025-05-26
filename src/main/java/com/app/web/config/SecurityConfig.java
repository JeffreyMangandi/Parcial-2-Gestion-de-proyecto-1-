package com.app.web.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ✅ INYECCIÓN DE VARIABLES DE ENTORNO
    @Value("${AUTH0_CLIENT_ID:qYwgcBm5ckiLY8L5Ka2jvjngLZR3rsIl}")
    private String auth0ClientId;
    
    @Value("${AUTH0_DOMAIN:dev-8p7c66amplkwoh3t.us.auth0.com}")
    private String auth0Domain;
    
    @Value("${AUTH0_LOGOUT_URI:http://localhost:8090/?logout=true}")
    private String auth0LogoutUri;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Rutas públicas
                .requestMatchers("/", "/login", "/login/**", "/oauth2/**", "/css/**", "/js/**", "/images/**", "/error", "/favicon.ico").permitAll()
                
                // Rutas que requieren rol ADMIN
                .requestMatchers("/creadores/nuevo", "/creadores/editar/**", "/creadores/eliminar/**").hasRole("ADMIN")
                .requestMatchers("/proyectos/nuevo", "/proyectos/editar/**", "/proyectos/{id}").hasRole("ADMIN")
                .requestMatchers("/reportes/**", "/analisis/**").hasRole("ADMIN")
                
                // Rutas que requieren autenticación (USER o ADMIN)
                .requestMatchers("/dashboard", "/proyectos", "/creadores", "/profile").authenticated()
                
                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            // Configurar login tradicional con formulario
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            // OAuth2 configuration
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=oauth2_error")
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(this.oidcUserService())
                )
            )
            // ✅ LOGOUT CON VARIABLES DE ENTORNO
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
                .logoutSuccessHandler((request, response, authentication) -> {
                    System.out.println("🚪 User logged out successfully");
                    
                    // Invalidar sesión completamente
                    request.getSession().invalidate();
                    
                    // Si es usuario OAuth2, hacer logout completo de Auth0
                    if (authentication != null && authentication.getPrincipal() instanceof OidcUser) {
                        System.out.println("🔗 OAuth2 user logout - redirecting to Auth0");
                        // ✅ USANDO VARIABLES DE ENTORNO
                        String logoutUrl = String.format(
                            "https://%s/v2/logout?client_id=%s&returnTo=%s",
                            auth0Domain, auth0ClientId, auth0LogoutUri
                        );
                        response.sendRedirect(logoutUrl);
                    } else {
                        // Usuario local - redirect a página principal
                        System.out.println("👤 Local user logout - redirecting to home");
                        response.sendRedirect("/?logout=true");
                    }
                })
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    System.out.println("❌ Authentication required for: " + request.getRequestURI());
                    System.out.println("❌ Exception: " + authException.getMessage());
                    response.sendRedirect("/login?auth_required=true");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    System.out.println("❌ Access denied for: " + request.getRequestURI());
                    response.sendRedirect("/dashboard?access_denied=true");
                })
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/oauth2/**", "/login/oauth2/**")
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/login?expired=true")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        PasswordEncoder encoder = passwordEncoder();
        
        System.out.println("🔐 Usuarios locales creados:");
        
        UserDetails admin = User.builder()
            .username("admin@test.com")
            .password(encoder.encode("admin123"))
            .roles("ADMIN")
            .build();
        System.out.println("✅ admin@test.com / admin123 (ADMIN)");
        
        UserDetails user = User.builder()
            .username("user@test.com")
            .password(encoder.encode("user123"))
            .roles("USER")
            .build();
        System.out.println("✅ user@test.com / user123 (USER)");

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);

            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            String email = oidcUser.getEmail();
            String subject = oidcUser.getSubject();
            String name = oidcUser.getFullName();
            
            System.out.println("=== 🔍 AUTH0 USER INFO DEBUG ===");
            System.out.println("Registration ID: " + registrationId);
            System.out.println("Subject (Auth0 ID): " + subject);
            System.out.println("Email: " + email);
            System.out.println("Full Name: " + name);
            System.out.println("Given Name: " + oidcUser.getGivenName());
            System.out.println("Family Name: " + oidcUser.getFamilyName());
            System.out.println("All Claims: " + oidcUser.getClaims());
            System.out.println("All Attributes: " + oidcUser.getAttributes());

            Collection<GrantedAuthority> authorities = new ArrayList<>();
            List<String> roles = null;
            
            // 🔧 ASIGNAR ROLES BASADO EN EMAIL DESDE AUTH0
            if (email != null) {
                if (email.equals("admin@test.com") || email.contains("admin")) {
                    roles = List.of("ADMIN");
                    System.out.println("🔑 Assigning ADMIN role to Auth0 user: " + email);
                } else {
                    // Por defecto, todos los usuarios de Auth0 tienen rol USER
                    roles = List.of("USER");
                    System.out.println("👤 Assigning USER role to Auth0 user: " + email);
                }
            } else {
                System.out.println("⚠️ No email found in Auth0 user, using default USER role");
                roles = List.of("USER");
            }

            // Convertir roles a authorities
            for (String role : roles) {
                String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase();
                authorities.add(new SimpleGrantedAuthority(authority));
            }

            System.out.println("✅ Final Auth0 authorities: " + authorities);
            System.out.println("===============================");

            return new DefaultOidcUser(
                authorities,
                oidcUser.getIdToken(),
                oidcUser.getUserInfo()
            );
        };
    }
}