package com.neoapp.controller;

import com.neoapp.dto.ClienteRequestDTO;
import com.neoapp.dto.ClienteResponseDTO;
import com.neoapp.exception.ApiResponse;
import com.neoapp.service.ClienteService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> criar(@RequestBody @Valid ClienteRequestDTO dto) {
        ClienteResponseDTO response = clienteService.criarCliente(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> atualizar(@PathVariable Long id, @RequestBody @Valid ClienteRequestDTO dto) {
        ClienteResponseDTO response = clienteService.atualizarCliente(id, dto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> buscarPorCpf(@PathVariable String cpf) {
        ClienteResponseDTO response = clienteService.buscarPorCpf(cpf);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> buscarPorId(@PathVariable Long id) {
        ClienteResponseDTO response = clienteService.buscarPorId(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/nome")
    @PageableAsQueryParam
    public ResponseEntity<ApiResponse<Page<ClienteResponseDTO>>> buscarPorNome(
            @RequestParam String nome,
            @ParameterObject @PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ClienteResponseDTO> response = clienteService.listarPorNome(nome, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    @PageableAsQueryParam
    public ResponseEntity<ApiResponse<Page<ClienteResponseDTO>>> listarTodos(
            @ParameterObject @PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ClienteResponseDTO> response = clienteService.listarClientes(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }


}
