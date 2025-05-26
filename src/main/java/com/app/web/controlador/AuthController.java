package com.app.web.controlador;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal Object principal, 
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        System.out.println("🏠 === HOME ENDPOINT ===");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // ✅ VERIFICAR SI VIENE DEL LOGOUT
        if ("true".equals(logout)) {
            System.out.println("✅ User logged out successfully - showing message");
            model.addAttribute("logoutMessage", "Has cerrado sesión correctamente");
        }
        
        // Verificar si está autenticado Y no es usuario anónimo
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            System.out.println("✅ User authenticated: " + auth.getName());
            System.out.println("✅ Redirecting to dashboard");
            return "redirect:/dashboard";
        }
        
        System.out.println("📝 No authenticated user, showing login");
        return "login";
    }

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal Object principal,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        System.out.println("🔐 === LOGIN ENDPOINT ===");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // ✅ VERIFICAR SI VIENE DEL LOGOUT
        if ("true".equals(logout)) {
            System.out.println("✅ User logged out successfully - showing message on login page");
            model.addAttribute("logoutMessage", "Has cerrado sesión correctamente");
        }
        
        // *** ARREGLO DEL BUCLE: Verificación más estricta ***
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            System.out.println("✅ User already authenticated: " + auth.getName());
            System.out.println("✅ Authorities: " + auth.getAuthorities());
            return "redirect:/dashboard";
        }
        
        System.out.println("📄 Showing login page");
        return "login";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal Object principal, Model model) {
        System.out.println("👤 === PROFILE ENDPOINT ===");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            System.out.println("❌ User not authenticated");
            return "redirect:/login";
        }
        
        String userName = "Usuario";
        String userEmail = "";
        boolean isAdmin = false;
        Object userObject = null;
        
        try {
            if (principal instanceof UserDetails) {
                // Login tradicional
                UserDetails userDetails = (UserDetails) principal;
                userName = userDetails.getUsername();
                userEmail = userDetails.getUsername();
                isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
                
                System.out.println("✅ Form Login User: " + userEmail);
                System.out.println("🔑 Is Admin: " + isAdmin);
                
                userObject = new UserProfileInfo(userEmail, userEmail, userDetails.getUsername());
                model.addAttribute("user", userObject);
                model.addAttribute("userRoles", userDetails.getAuthorities());
                
            } else if (principal instanceof OidcUser) {
                // OAuth2 login
                OidcUser oidcUser = (OidcUser) principal;
                String fullName = oidcUser.getFullName();
                userEmail = oidcUser.getEmail();
                
                if (fullName != null && !fullName.isEmpty()) {
                    userName = fullName;
                } else if (userEmail != null && !userEmail.isEmpty()) {
                    userName = userEmail;
                }
                
                isAdmin = oidcUser.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
                
                System.out.println("✅ OAuth2 User: " + userEmail);
                System.out.println("🔑 Is Admin: " + isAdmin);
                
                model.addAttribute("user", oidcUser);
                model.addAttribute("userRoles", oidcUser.getAuthorities());
            } else {
                System.out.println("⚠️ Unknown principal type");
                userName = auth.getName();
                userEmail = auth.getName();
                isAdmin = auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
                
                userObject = new UserProfileInfo(userName, userEmail, userName);
                model.addAttribute("user", userObject);
                model.addAttribute("userRoles", auth.getAuthorities());
            }
            
            model.addAttribute("userName", userName);
            model.addAttribute("userEmail", userEmail);
            model.addAttribute("isAdmin", isAdmin);
            
            System.out.println("📊 Profile loaded for: " + userName);
            
        } catch (Exception e) {
            System.out.println("❌ Error processing profile: " + e.getMessage());
            return "redirect:/dashboard?error=profile_error";
        }
        
        return "profile";
    }
    
    // Clase auxiliar
    public static class UserProfileInfo {
        private final String fullName;
        private final String email;
        private final String subject;
        
        public UserProfileInfo(String fullName, String email, String subject) {
            this.fullName = fullName;
            this.email = email;
            this.subject = subject;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public String getEmail() {
            return email;
        }
        
        public String getSubject() {
            return subject;
        }
        
        public java.time.Instant getUpdatedAt() {
            return java.time.Instant.now();
        }
    }
}