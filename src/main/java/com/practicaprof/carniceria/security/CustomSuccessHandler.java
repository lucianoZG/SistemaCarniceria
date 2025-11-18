/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.practicaprof.carniceria.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Obtenemos el rol del usuario autenticado
        String rol = authentication.getAuthorities().iterator().next().getAuthority();

        // Redirigimos según el rol
        if (rol.equals("ROLE_ADMIN")) {
            response.sendRedirect("/index");
        } else if (rol.equals("ROLE_CLIENTE")) {
            response.sendRedirect("/cliente/indexCliente");
        } else {
            response.sendRedirect("/login?error");
        }
    }
}
