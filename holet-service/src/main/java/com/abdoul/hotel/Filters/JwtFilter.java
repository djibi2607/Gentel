package com.abdoul.hotel.Filters;

import com.abdoul.hotel.Entities.UserModel;
import com.abdoul.hotel.Repositories.UserRepository;
import com.abdoul.hotel.Utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwt;
    private final UserRepository userRepository;

    public JwtFilter (JwtUtil jwt, UserRepository userRepository){
        this.jwt = jwt;
        this.userRepository = userRepository;
    }

    @Override
    public void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String token = request.getHeader("Authorization");

        if (token == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Token not found\"}");
            return;
        }

        if (!token.startsWith("Bearer ")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid token\"}");
            return;
        }

        String cleanToken = token.split(" ")[1];

        if (!jwt.isTokenValid(cleanToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authentication failed\"}");
            return;
        }

        Long userId = Long.parseLong(jwt.extractIdFromToken(cleanToken));

        UserModel currentUser = userRepository.findById(userId).orElse(null);

        if (currentUser == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"User not found\"}");
            return;
        }

        request.setAttribute("currentUser", currentUser);

        filterChain.doFilter(request, response);
    }
}
