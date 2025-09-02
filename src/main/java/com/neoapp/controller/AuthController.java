package com.neoapp.controller;

import com.neoapp.dto.AuthRequestDTO;
import com.neoapp.dto.AuthResponseDTO;
import com.neoapp.dto.RegisterRequestDTO;
import com.neoapp.dto.UsuarioResponseDTO;
import com.neoapp.exception.ApiResponse;
import com.neoapp.security.JwtUtil;
import com.neoapp.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> createAuthenticationToken(@RequestBody AuthRequestDTO authRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.nome(), authRequest.senha()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.nome());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDTO(jwt));
    }

    @PostMapping("/register")

    public ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        usuarioService.registrarNovoUsuario(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}