package com.app.web.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.web.servicio.CreadorServicio;
import com.app.web.servicio.ProyectoServicio;

@Controller
public class DashboardControlador {

    @Autowired
    private ProyectoServicio proyectoServicio;
    
    @Autowired
    private CreadorServicio creadorServicio;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal Object principal, Model model) {
        System.out.println("🏠 === DASHBOARD ACCESS ATTEMPT ===");
        
        // Obtener información de autenticación
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔍 Authentication: " + (auth != null ? auth.getName() : "null"));
        System.out.println("🔍 Is Authenticated: " + (auth != null ? auth.isAuthenticated() : "false"));
        System.out.println("🔍 Principal Type: " + (principal != null ? principal.getClass().getSimpleName() : "null"));
        System.out.println("🔍 Principal: " + principal);
        
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String userName = "Usuario";
            String userEmail = "";
            boolean isAdmin = false;
            
            // Determinar el tipo de usuario (Form login vs OAuth2)
            if (principal instanceof UserDetails) {
                // Login tradicional (form)
                UserDetails userDetails = (UserDetails) principal;
                userName = userDetails.getUsername();
                userEmail = userDetails.getUsername(); // En nuestro caso, username es el email
                isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(auth2 -> auth2.getAuthority().equals("ROLE_ADMIN"));
                
                System.out.println("✅ Form Login User Detected");
                System.out.println("📧 Email: " + userEmail);
                System.out.println("👤 Username: " + userName);
                System.out.println("🔑 Is Admin: " + isAdmin);
                System.out.println("🎭 Authorities: " + userDetails.getAuthorities());
                
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
                    .anyMatch(auth2 -> auth2.getAuthority().equals("ROLE_ADMIN"));
                
                System.out.println("✅ OAuth2 User Detected");
                System.out.println("📧 Email: " + userEmail);
                System.out.println("👤 Full Name: " + userName);
                System.out.println("🔑 Is Admin: " + isAdmin);
                System.out.println("🎭 Authorities: " + oidcUser.getAuthorities());
            }
            
            // Obtener estadísticas para el dashboard
            Long totalProyectos = (long) proyectoServicio.listarTodosLosProyectos().size();
            Long totalCreadores = (long) creadorServicio.listarTodosLosCreadores().size();
            
            // Añadir atributos al modelo
            model.addAttribute("userName", userName);
            model.addAttribute("userEmail", userEmail);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("totalProyectos", totalProyectos);
            model.addAttribute("totalCreadores", totalCreadores);
            
            System.out.println("📊 Dashboard Stats - Proyectos: " + totalProyectos + ", Creadores: " + totalCreadores);
            System.out.println("✅ Returning dashboard view");
            return "dashboard";
        }
        
        System.out.println("❌ No authenticated user found, redirecting to login");
        return "redirect:/login";
    }
}