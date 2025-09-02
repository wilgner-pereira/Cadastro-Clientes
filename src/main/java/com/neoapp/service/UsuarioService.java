package com.neoapp.service;

import com.neoapp.dto.RegisterRequestDTO;
import com.neoapp.dto.UsuarioResponseDTO;

public interface UsuarioService {
    UsuarioResponseDTO registrarNovoUsuario(RegisterRequestDTO registerRequest);
}