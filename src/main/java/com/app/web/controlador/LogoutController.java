package com.app.web.controlador;

// ✅ CORREGIDO: Jakarta EE en lugar de Java EE
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LogoutController {

    @PostMapping("/logout")
    public String logoutPost(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("🚪 === LOGOUT POST REQUEST ===");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            System.out.println("🔓 Logging out user: " + auth.getName());
            new SecurityContextLogoutHandler().logout(request, response, auth);
            System.out.println("✅ POST logout completed");
        }
        
        return "redirect:/login?logout=true";
    }
}