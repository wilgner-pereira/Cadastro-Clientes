package com.neoapp.service;

import com.neoapp.dto.ClienteRequestDTO;
import com.neoapp.dto.ClienteResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {
    ClienteResponseDTO criarCliente(ClienteRequestDTO dto);
    ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO dto);
    void deletarCliente(Long id);
    ClienteResponseDTO buscarPorCpf(String cpf);
    ClienteResponseDTO buscarPorId(Long id);
    Page<ClienteResponseDTO> listarClientes(Pageable pageable);
    Page<ClienteResponseDTO> listarPorNome(String nome, Pageable pageable);

}
