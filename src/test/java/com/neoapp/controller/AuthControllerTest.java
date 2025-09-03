package com.neoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoapp.dto.AuthRequestDTO;
import com.neoapp.dto.RegisterRequestDTO;
import com.neoapp.dto.UsuarioResponseDTO;
import com.neoapp.exception.BusinessRuleException;
import com.neoapp.exception.ErrorCode;
import com.neoapp.security.JwtUtil;
import com.neoapp.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Desabilita os filtros de segurança para focar no controller
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mockamos todas as dependências do AuthController
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UsuarioService usuarioService;

    // =================== TESTES DE REGISTRO (/register) ===================

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso e retornar status 201")
    void registerUser_comDadosValidos_deveRetornar201() throws Exception {
        // ARRANGE
        RegisterRequestDTO request = new RegisterRequestDTO("novoUsuario", "senha123");
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO("novoUsuario");

        // Simula o comportamento do serviço
        when(usuarioService.registrarNovoUsuario(any(RegisterRequestDTO.class))).thenReturn(responseDTO);

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("novoUsuario"));
    }

    @Test
    @DisplayName("Não deve registrar usuário com username inválido (em branco) e deve retornar status 400")
    void registerUser_comUsernameInvalido_deveRetornar400() throws Exception {
        // ARRANGE
        // O DTO de registro agora tem validações (@Valid no controller irá ativá-las)
        RegisterRequestDTO requestInvalida = new RegisterRequestDTO("", "senha123");

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalida)))
                .andExpect(status().isBadRequest()); // A validação do DTO deve falhar
    }

    // =================== TESTES DE LOGIN (/login) ===================

    @Test
    @DisplayName("Deve autenticar com credenciais válidas e retornar um token JWT")
    void login_comCredenciaisValidas_deveRetornarToken() throws Exception {
        // ARRANGE
        AuthRequestDTO authRequest = new AuthRequestDTO("usuarioValido", "senhaCorreta");
        UserDetails userDetails = new User("usuarioValido", "senhaCriptografada", Collections.emptyList());

        // Simula o comportamento dos componentes de segurança
        when(userDetailsService.loadUserByUsername("usuarioValido")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("meu-token-jwt-fake");

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("meu-token-jwt-fake"));
    }

    @Test
    @DisplayName("Não deve autenticar com credenciais inválidas e deve retornar status 401")
    void login_comCredenciaisInvalidas_deveRetornar401() throws Exception {
        // ARRANGE
        AuthRequestDTO authRequest = new AuthRequestDTO("usuarioValido", "senhaErrada");

        // Simula o AuthenticationManager lançando uma exceção de credenciais inválidas
        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager).authenticate(any());

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized()); // O GlobalExceptionHandler ou o Spring Security deve tratar isso como 401
    }
}