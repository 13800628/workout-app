package com.workout.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;     

import com.workout.exception.user.UserDomainException;
import com.workout.service.CustomUserDetailsService;

import org.springframework.lang.NonNull;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
  
  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain)
                                   throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer")) {
      String token = authHeader.substring(7);

      if (jwtUtil.validateToken(token)) {
        try {
          Long userId = jwtUtil.extraUserId(token);
          UserDetails userDetails = userDetailsService.loadUserById(userId);
          UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (UsernameNotFoundException | NumberFormatException e) {
          // 認証情報をセットせず後続に委ねる
          SecurityContextHolder.clearContext();
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}
