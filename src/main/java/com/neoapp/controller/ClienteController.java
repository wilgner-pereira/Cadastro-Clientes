package com.neoapp.controller;

import com.neoapp.dto.ClienteRequestDTO;
import com.neoapp.dto.ClienteResponseDTO;
import com.neoapp.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ClienteResponseDTO criar(@RequestBody @Valid ClienteRequestDTO dto) {
        return clienteService.criarCliente(dto);
    }

    @PutMapping("/{id}")
    public ClienteResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid ClienteRequestDTO dto) {
        return clienteService.atualizarCliente(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        clienteService.deletarCliente(id);
    }

    @GetMapping("/cpf/{cpf}")
    public ClienteResponseDTO buscarPorCpf(@PathVariable String cpf) {
        return clienteService.buscarPorCpf(cpf);
    }

    @GetMapping("/{id}")
    public ClienteResponseDTO buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    @GetMapping("/nome")
    public Page<ClienteResponseDTO> buscarPorNome(@RequestParam String nome, Pageable pageable) {
        return clienteService.listarPorNome(nome, pageable);
    }

    @GetMapping
    public Page<ClienteResponseDTO> listarTodos(Pageable pageable) {
        return clienteService.listarClientes(pageable);
    }


}
