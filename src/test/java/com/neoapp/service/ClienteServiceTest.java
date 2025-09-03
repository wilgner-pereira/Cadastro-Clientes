package com.neoapp.service;

import com.neoapp.dto.ClienteMapper;
import com.neoapp.dto.ClienteRequestDTO;
import com.neoapp.dto.ClienteResponseDTO;
import com.neoapp.exception.BusinessRuleException;
import com.neoapp.exception.ErrorCode;
import com.neoapp.exception.ResourceNotFoundException;
import com.neoapp.model.Cliente;
import com.neoapp.repository.ClienteRepository;
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

    @Nested
    @DisplayName("Testes de atualizarCliente")
    class atualizarCliente{
        @Test
        void atualizarCliente_valido_retornaDTO() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(clienteRepository.findByCpf(requestDTO.cpf())).thenReturn(Optional.of(cliente));
            when(clienteRepository.findByEmail(requestDTO.email())).thenReturn(Optional.of(cliente));
            doNothing().when(mapper).updateClienteFromDto(requestDTO, cliente);
            when(clienteRepository.save(clienteCaptor.capture())).thenReturn(cliente);
            when(mapper.toDto(cliente)).thenReturn(responseDTO);

            ClienteResponseDTO result = clienteService.atualizarCliente(1L, requestDTO);

            assertNotNull(result);
            assertEquals(responseDTO, result);

            Cliente atualizado = clienteCaptor.getValue();
            assertEquals(requestDTO.cpf(), atualizado.getCpf());
            assertEquals(requestDTO.nome(), atualizado.getNome());
            assertEquals(requestDTO.email(), atualizado.getEmail());
            assertEquals(requestDTO.dataNascimento(), atualizado.getDataNascimento());

            verify(clienteRepository).findById(1L);
            verify(clienteRepository).findByCpf(requestDTO.cpf());
            verify(clienteRepository).findByEmail(requestDTO.email());
            verify(clienteRepository).save(any(Cliente.class));
            verify(mapper).updateClienteFromDto(requestDTO, cliente);
            verify(mapper).toDto(cliente);
            verifyNoMoreInteractions(clienteRepository, mapper);
        }

        @Test
        void atualizarCliente_naoExiste_lancaException() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> clienteService.atualizarCliente(1L, requestDTO));

            assertEquals(ErrorCode.CLIENTE_NOT_FOUND, ex.getErrorCode());
            verify(clienteRepository).findById(1L);
            verifyNoMoreInteractions(clienteRepository);
            verifyNoInteractions(mapper);
        }

        @Test
        void atualizarCliente_cpfDuplicado_lancaBusinessRuleException() {
            Cliente outro = new Cliente();
            outro.setId(2L);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(clienteRepository.findByCpf(requestDTO.cpf())).thenReturn(Optional.of(outro));

            BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                    () -> clienteService.atualizarCliente(1L, requestDTO));

            assertEquals(ErrorCode.CPF_ALREADY_EXISTS, ex.getErrorCode());
        }

        @Test
        void atualizarCliente_emailDuplicado_lancaBusinessRuleException() {
            Cliente outro = new Cliente();
            outro.setId(2L);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(clienteRepository.findByCpf(requestDTO.cpf())).thenReturn(Optional.of(cliente));
            when(clienteRepository.findByEmail(requestDTO.email())).thenReturn(Optional.of(outro));

            BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                    () -> clienteService.atualizarCliente(1L, requestDTO));

            assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, ex.getErrorCode());
        }
    }
    @Nested
    @DisplayName("Testes de deletarCliente")
    class deletarCliente{
        @Test
        void deletarCliente_existe_deleta() {
            when(clienteRepository.existsById(1L)).thenReturn(true);

            clienteService.deletarCliente(1L);

            verify(clienteRepository).existsById(1L);
            verify(clienteRepository).deleteById(1L);
            verifyNoMoreInteractions(clienteRepository);
        }

        @Test
        void deletarCliente_naoExiste_lancaException() {
            when(clienteRepository.existsById(1L)).thenReturn(false);

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> clienteService.deletarCliente(1L));

            assertEquals(ErrorCode.CLIENTE_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Testes de buscaPorCpf")
    class buscaPorCpf{
        @Test
        void buscarPorCpf_existe_retornaDTO() {
            when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.of(cliente));
            when(mapper.toDto(cliente)).thenReturn(responseDTO);

            ClienteResponseDTO result = clienteService.buscarPorCpf(cliente.getCpf());

            assertEquals(responseDTO, result);
            verify(clienteRepository).findByCpf(cliente.getCpf());
            verify(mapper).toDto(cliente);
        }

        @Test
        void buscarPorCpf_naoExiste_lancaException() {
            when(clienteRepository.findByCpf("123")).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> clienteService.buscarPorCpf("123"));

            assertEquals(ErrorCode.CLIENTE_NOT_FOUND, ex.getErrorCode());
        }
    }


    @Nested
    @DisplayName("Testes de buscaPorId")
    class buscaPorId{
        @Test
        void buscarPorId_existe_retornaDTO() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(mapper.toDto(cliente)).thenReturn(responseDTO);

            ClienteResponseDTO result = clienteService.buscarPorId(1L);

            assertEquals(responseDTO, result);
            verify(clienteRepository).findById(1L);
            verify(mapper).toDto(cliente);
        }

        @Test
        void buscarPorId_naoExiste_lancaException() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> clienteService.buscarPorId(1L));

            assertEquals(ErrorCode.CLIENTE_NOT_FOUND, ex.getErrorCode());
        }
    }


    @Nested
    @DisplayName("Testes para buscas paginadas")
    class buscaPaginadas{
        @Test
        void listarPorNome_retornaPaginaDTO() {
            Page<Cliente> page = new PageImpl<>(List.of(cliente));
            when(clienteRepository.findByNomeContainingIgnoreCase("Teste", PageRequest.of(0, 10))).thenReturn(page);
            when(mapper.toDto(cliente)).thenReturn(responseDTO);

            Page<ClienteResponseDTO> result = clienteService.listarPorNome("Teste", PageRequest.of(0, 10));

            assertEquals(1, result.getContent().size());
            assertEquals(responseDTO, result.getContent().getFirst());
        }

        @Test
        void listarClientes_retornaPaginaDTO() {
            Page<Cliente> page = new PageImpl<>(List.of(cliente));
            when(clienteRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);
            when(mapper.toDto(cliente)).thenReturn(responseDTO);

            Page<ClienteResponseDTO> result = clienteService.listarClientes(PageRequest.of(0, 10));

            assertEquals(1, result.getContent().size());
            assertEquals(responseDTO, result.getContent().getFirst());
        }
    }

}

