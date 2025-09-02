package com.neoapp.service;

import com.neoapp.dto.RegisterRequestDTO;
import com.neoapp.dto.UsuarioResponseDTO;
import com.neoapp.model.Usuario;

public interface UsuarioService {
    UsuarioResponseDTO registrarNovoUsuario(RegisterRequestDTO registerRequest);
}