package com.neoapp.service;

import com.neoapp.dto.ClienteRequestDTO;
import com.neoapp.dto.ClienteResponseDTO;

public interface ClienteService {
    ClienteResponseDTO criarCliente(ClienteRequestDTO dto);
    ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO dto);
}
