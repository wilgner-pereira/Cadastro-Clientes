package com.neoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoapp.dto.ClienteRequestDTO;
import com.neoapp.dto.ClienteResponseDTO;
import com.neoapp.exception.BusinessRuleException;
import com.neoapp.exception.ErrorCode;
import com.neoapp.exception.ResourceNotFoundException;
import com.neoapp.security.JwtUtil;
import com.neoapp.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false) // Desabilita filtros de segurança para focar no controller
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private ClienteRequestDTO clienteRequestDTO;
    private ClienteResponseDTO clienteResponseDTO;

    @BeforeEach
    void setUp() {
        // Cria objetos DTO reutilizáveis para os testes
        clienteRequestDTO = new ClienteRequestDTO(
                "68691814039",
                "Fulano de Tal",
                LocalDate.of(1990, 1, 15),
                "fulano@email.com"
        );

        clienteResponseDTO = new ClienteResponseDTO(
                1L,
                "68691814039",
                "Fulano de Tal",
                LocalDate.of(1990, 1, 15),
                "fulano@email.com",
                LocalDateTime.now()
        );
    }


    @Nested
    @DisplayName("Testes para criar Clientes")
    class criarClientes{
        @Test
        @DisplayName("Deve criar um cliente com sucesso e retornar status 201")
        void criar_comDadosValidos_deveRetornar201() throws Exception {
            // ARRANGE
            when(clienteService.criarCliente(any(ClienteRequestDTO.class))).thenReturn(clienteResponseDTO);

            // ACT & ASSERT
            mockMvc.perform(post("/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.nome", is("Fulano de Tal")))
                    .andExpect(jsonPath("$.data.cpf", is("68691814039")));
        }

        @Test
        @DisplayName("Não deve criar cliente com CPF duplicado e deve retornar status 400")
        void criar_comCpfDuplicado_deveRetornar400() throws Exception {
            // ARRANGE
            when(clienteService.criarCliente(any(ClienteRequestDTO.class)))
                    .thenThrow(new BusinessRuleException(ErrorCode.CPF_ALREADY_EXISTS));

            // ACT & ASSERT
            mockMvc.perform(post("/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.code").value(ErrorCode.CPF_ALREADY_EXISTS.name()));
        }
    }


    @Nested
    @DisplayName("Testes para atualizar clientes")
    class atualizarCliente{
        @Test
        @DisplayName("Deve atualizar um cliente com sucesso e retornar status 200")
        void atualizar_comDadosValidos_deveRetornar200() throws Exception {
            // ARRANGE
            when(clienteService.atualizarCliente(anyLong(), any(ClienteRequestDTO.class))).thenReturn(clienteResponseDTO);

            // ACT & ASSERT
            mockMvc.perform(put("/clientes/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id", is(1)));
        }

        @Test
        @DisplayName("Não deve atualizar cliente inexistente e deve retornar status 404")
        void atualizar_comIdInexistente_deveRetornar404() throws Exception {
            // ARRANGE
            when(clienteService.atualizarCliente(anyLong(), any(ClienteRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException(ErrorCode.CLIENTE_NOT_FOUND));

            // ACT & ASSERT
            mockMvc.perform(put("/clientes/{id}", 99L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isNotFound());
        }
    }


    @Test
    @DisplayName("Deve deletar um cliente com sucesso e retornar status 204")
    void deletar_comIdExistente_deveRetornar204() throws Exception {
        // ARRANGE
        doNothing().when(clienteService).deletarCliente(anyLong());

        // ACT & ASSERT
        mockMvc.perform(delete("/clientes/{id}", 1L))
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso e retornar status 200")
    void buscarPorId_comIdExistente_deveRetornar200() throws Exception {
        // ARRANGE
        when(clienteService.buscarPorId(1L)).thenReturn(clienteResponseDTO);

        // ACT & ASSERT
        mockMvc.perform(get("/clientes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(1)));
    }

    @Test
    @DisplayName("Deve listar todos os clientes de forma paginada e retornar status 200")
    void listarTodos_deveRetornarPaginaDeClientes() throws Exception {
        // ARRANGE
        Page<ClienteResponseDTO> paginaDeClientes = new PageImpl<>(List.of(clienteResponseDTO));
        when(clienteService.listarClientes(any())).thenReturn(paginaDeClientes);

        // ACT & ASSERT
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].nome", is("Fulano de Tal")));
    }
}