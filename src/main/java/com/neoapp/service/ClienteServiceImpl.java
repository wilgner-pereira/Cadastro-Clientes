package com.neoapp.service;

import com.neoapp.dto.ClienteMapper;
import com.neoapp.dto.ClienteRequestDTO;
import com.neoapp.dto.ClienteResponseDTO;
import com.neoapp.model.Cliente;
import com.neoapp.repository.ClienteRepository;

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
            throw new RuntimeException("CPF já cadastrado");
        }
        Cliente cliente = mapper.toEntity(dto);
        Cliente salvo = clienteRepository.save(cliente);
        return mapper.toDto(salvo);
    }

    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        clienteRepository.findByCpf(dto.cpf())
                .filter(c -> !c.getId().equals(id))
                        .ifPresent(c -> {throw new RuntimeException("CPF já cadastrado");});
        //  Alterações em dados imutáveis não são ideais. No entanto, como este sistema de cadastro faz pesquisa de cadastros
        //  entendo que ele seria usado pela própria empresa que cadastra e erros podem ocorrer no momento do cadastro, temos varias abordagens:
        //  exclusão do registro e novo cadastro do cliente,
        //  atualização do CPF, que pode ser controlada com validação, alteração via login de administrador,
        //  ou registro de logs de alterações.
        //  Para nosso caso vou permitir a atualização de todos os campos.
        cliente.setCpf(dto.cpf());
        //O mesmo caso do CPF se aplica ao nome.
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        //O mesmo caso do CPF se aplica a data de nascimento.
        cliente.setDataNascimento(dto.dataNascimento());
        Cliente atualizado = clienteRepository.save(cliente);
        return mapper.toDto(atualizado);
    }
}
