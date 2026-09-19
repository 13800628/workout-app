package com.workout.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workout.config.CustomUserDetails;
import com.workout.config.JwtUtil;
import com.workout.dto.auth.AuthRequest;
import com.workout.dto.auth.AuthResponse;
import com.workout.exception.user.UserDomainException;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
  
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
  }

  // 現状400で許容しているので今後エラーメッセージの詳細が欲しくなるのであれば401での修正を検討
  @PostMapping("/login")
  public ResponseEntity<AuthResponse>login(@Valid @RequestBody AuthRequest request) {
    try {
      Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          request.username(),
          request.password()
        )
      );
      CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
      String token = jwtUtil.generateToken(principal.getUserId());
      return ResponseEntity.ok(new AuthResponse(token));
    } catch (AuthenticationException e) {
      throw UserDomainException.invalid("ユーザー名またはパスワードが正しくありません");
    }
  }
  
}
