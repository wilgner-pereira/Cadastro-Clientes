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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UsuarioService usuarioService;

    @Nested
    @DisplayName("Testes para registro de usuario")
    class registrarUsuario{
        @Test
        @DisplayName("Deve registrar um novo usuário com sucesso e retornar status 201")
        void registerUser_comDadosValidos_deveRetornar201() throws Exception {

            RegisterRequestDTO request = new RegisterRequestDTO("novoUsuario", "senha123");
            UsuarioResponseDTO responseDTO = new UsuarioResponseDTO("novoUsuario");
            when(usuarioService.registrarNovoUsuario(any(RegisterRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.username").value("novoUsuario"));
        }

        @Test
        @DisplayName("Não deve registrar usuário duplicado e deve retornar status 400")
        void registerUser_comUsernameDuplicado_deveRetornar400() throws Exception {

            RegisterRequestDTO request = new RegisterRequestDTO("usuarioExistente", "senha123");
            when(usuarioService.registrarNovoUsuario(any(RegisterRequestDTO.class)))
                    .thenThrow(new BusinessRuleException(ErrorCode.USUARIO_ALREADY_EXISTS));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.code").value(ErrorCode.USUARIO_ALREADY_EXISTS.name()));
        }

        @Test
        @DisplayName("Não deve registrar usuário com username em branco e deve retornar status 400")
        void registerUser_comUsernameInvalido_deveRetornar400() throws Exception {

            RegisterRequestDTO requestInvalida = new RegisterRequestDTO("", "senha123");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalida)))
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("Testes para login de usuario")
    class loginUsuario{
        @Test
        @DisplayName("Deve autenticar com credenciais válidas e retornar um token JWT")
        void login_comCredenciaisValidas_deveRetornarToken() throws Exception {

            AuthRequestDTO authRequest = new AuthRequestDTO("usuarioValido", "senhaCorreta");
            UserDetails userDetails = new User("usuarioValido", "senhaCriptografada", Collections.emptyList());
            when(userDetailsService.loadUserByUsername("usuarioValido")).thenReturn(userDetails);
            when(jwtUtil.generateToken(userDetails)).thenReturn("meu-token-jwt-fake");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("meu-token-jwt-fake"));
        }

        @Test
        @DisplayName("Não deve autenticar com credenciais inválidas e deve retornar status 401")
        void login_comCredenciaisInvalidas_deveRetornar401() throws Exception {

            AuthRequestDTO authRequest = new AuthRequestDTO("usuarioValido", "senhaErrada");
            doThrow(new BadCredentialsException("Credenciais inválidas"))
                    .when(authenticationManager).authenticate(any());

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Não deve autenticar se o usuário não for encontrado e deve retornar status 401")
        void login_quandoUsuarioNaoEncontrado_deveRetornar401() throws Exception {

            AuthRequestDTO authRequest = new AuthRequestDTO("usuarioInexistente", "senha123");
            when(userDetailsService.loadUserByUsername("usuarioInexistente"))
                    .thenThrow(new UsernameNotFoundException("Usuário não encontrado"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Não deve autenticar com username em branco e deve retornar status 400")
        void login_comUsernameEmBranco_deveRetornar400() throws Exception {

            AuthRequestDTO requestInvalida = new AuthRequestDTO("", "senha123");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalida)))
                    .andExpect(status().isBadRequest());
        }
    }
}