package com.neoapp;

import com.neoapp.dto.ClienteMapper;
import com.neoapp.dto.ClienteRequestDTO;
import com.neoapp.dto.ClienteResponseDTO;
import com.neoapp.exception.BusinessRuleException;
import com.neoapp.exception.ErrorCode;
import com.neoapp.exception.ResourceNotFoundException;
import com.neoapp.model.Cliente;
import com.neoapp.repository.ClienteRepository;
import com.neoapp.service.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper mapper;

    @Captor
    private ArgumentCaptor<Cliente> clienteCaptor;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private ClienteRequestDTO requestDTO;
    private ClienteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        LocalDate dataNascimento = LocalDate.of(1990, 1, 1);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCpf("12345678900");
        cliente.setNome("Cliente Teste");
        cliente.setEmail("teste@email.com");
        cliente.setDataNascimento(dataNascimento);
        cliente.setCreatedAt(LocalDateTime.now());

        requestDTO = new ClienteRequestDTO(
                "12345678900",
                "Cliente Teste",
                dataNascimento,
                "teste@email.com"
        );

        responseDTO = new ClienteResponseDTO(
                1L,
                "12345678900",
                "Cliente Teste",
                dataNascimento,
                "teste@email.com",
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("Testes de criarCliente")
    class criarClientes{
        void criarCliente_valido_retornaDTO_comIdadeCorreta() {
            // Cenário: CPF e email não existem
            when(clienteRepository.findByCpf(requestDTO.cpf())).thenReturn(Optional.empty());
            when(clienteRepository.findByEmail(requestDTO.email())).thenReturn(Optional.empty());
            when(mapper.toEntity(requestDTO)).thenReturn(cliente);
            when(clienteRepository.save(clienteCaptor.capture())).thenReturn(cliente);
            when(mapper.toDto(cliente)).thenReturn(responseDTO);

            ClienteResponseDTO result = clienteService.criarCliente(requestDTO);

            assertNotNull(result);
            assertEquals(responseDTO, result);

            // Verifica se a idade foi calculada corretamente
            int idadeEsperada = Period.between(responseDTO.dataNascimento(), LocalDate.now()).getYears();
            assertEquals(idadeEsperada, result.idade());

            // Verifica se o cliente passado para salvar é exatamente o que esperamos
            Cliente clienteSalvo = clienteCaptor.getValue();
            assertEquals(requestDTO.cpf(), clienteSalvo.getCpf());
            assertEquals(requestDTO.nome(), clienteSalvo.getNome());
            assertEquals(requestDTO.email(), clienteSalvo.getEmail());
            assertEquals(requestDTO.dataNascimento(), clienteSalvo.getDataNascimento());

            verify(clienteRepository).findByCpf(requestDTO.cpf());
            verify(clienteRepository).findByEmail(requestDTO.email());
            verify(clienteRepository).save(any(Cliente.class));
            verifyNoMoreInteractions(clienteRepository);
            verify(mapper).toEntity(requestDTO);
            verify(mapper).toDto(cliente);
            verifyNoMoreInteractions(mapper);
        }

        @Test
        void criarCliente_comCpfExistente_lancaBusinessRuleException() {
            when(clienteRepository.findByCpf(requestDTO.cpf())).thenReturn(Optional.of(cliente));

            BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                    () -> clienteService.criarCliente(requestDTO));

            assertEquals(ErrorCode.CPF_ALREADY_EXISTS, ex.getErrorCode());

            verify(clienteRepository).findByCpf(requestDTO.cpf());
            verifyNoMoreInteractions(clienteRepository);
            verifyNoInteractions(mapper);
        }

        @Test
        void criarCliente_comEmailExistente_lancaBusinessRuleException() {
            when(clienteRepository.findByCpf(requestDTO.cpf())).thenReturn(Optional.empty());
            when(clienteRepository.findByEmail(requestDTO.email())).thenReturn(Optional.of(cliente));

            BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                    () -> clienteService.criarCliente(requestDTO));

            assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, ex.getErrorCode());

            verify(clienteRepository).findByCpf(requestDTO.cpf());
            verify(clienteRepository).findByEmail(requestDTO.email());
            verifyNoMoreInteractions(clienteRepository);
            verifyNoInteractions(mapper);
        }
    }




}

