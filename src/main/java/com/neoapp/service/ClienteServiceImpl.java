package com.neoapp.service;

import com.neoapp.dto.ClienteMapper;
import com.neoapp.dto.ClienteRequestDTO;
import com.neoapp.dto.ClienteResponseDTO;
import com.neoapp.exception.BusinessRuleException;
import com.neoapp.exception.ErrorCode;
import com.neoapp.exception.ResourceNotFoundException;
import com.neoapp.model.Cliente;
import com.neoapp.repository.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClienteServiceImpl implements ClienteService{

    private final ClienteRepository clienteRepository;
    private final ClienteMapper mapper;

    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper mapper) {
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
    }

    @Override
    public ClienteResponseDTO criarCliente(ClienteRequestDTO dto) {
        if (clienteRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new BusinessRuleException(ErrorCode.CPF_ALREADY_EXISTS);
        }
        if(clienteRepository.findByEmail(dto.email()).isPresent()){
            throw new BusinessRuleException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        Cliente cliente = mapper.toEntity(dto);
        cliente.setCreatedAt(LocalDateTime.now());
        Cliente salvo = clienteRepository.save(cliente);
        return mapper.toDto(salvo);
    }

    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CLIENTE_NOT_FOUND));
        clienteRepository.findByCpf(dto.cpf())
                .filter(c -> !c.getId().equals(id))
                        .ifPresent(c -> {throw new BusinessRuleException(ErrorCode.CPF_ALREADY_EXISTS);});
        clienteRepository.findByEmail(dto.email())
                .filter(c -> !c.getId().equals(id))
                        .ifPresent(c -> {throw new BusinessRuleException(ErrorCode.EMAIL_ALREADY_EXISTS);});
        //  Alterações em dados imutáveis não são ideais. No entanto, como este sistema de cadastro faz pesquisa de cadastros
        //  entendo que ele seria usado pela própria empresa que cadastra e erros podem ocorrer no momento do cadastro, temos varias abordagens:
        //  exclusão do registro e novo cadastro do cliente,
        //  atualização que pode ser controlada com validação, alteração via login de administrador,
        //  ou registro de logs de alterações.
        //  Para nosso caso vou permitir a atualização de todos os campos.
        mapper.updateClienteFromDto(dto, cliente);
        Cliente atualizado = clienteRepository.save(cliente);
        return mapper.toDto(atualizado);
    }

    public void deletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorCode.CLIENTE_NOT_FOUND);
        }
        clienteRepository.deleteById(id);
    }

    public ClienteResponseDTO buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CLIENTE_NOT_FOUND));
        return mapper.toDto(cliente);
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CLIENTE_NOT_FOUND));
        return mapper.toDto(cliente);
    }

    // Busca por nome (parcial, paginada)
    public Page<ClienteResponseDTO> listarPorNome(String nome, Pageable pageable) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(mapper::toDto);
    }

    // Busca completa(paginada)
    public Page<ClienteResponseDTO> listarClientes(Pageable pageable) {
        return clienteRepository.findAll(pageable).map(mapper::toDto);
    }


}
