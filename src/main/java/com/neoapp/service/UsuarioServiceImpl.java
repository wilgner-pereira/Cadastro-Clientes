package com.neoapp.service;

import com.neoapp.dto.RegisterRequestDTO;

import com.neoapp.dto.UsuarioResponseDTO;
import com.neoapp.exception.BusinessRuleException;
import com.neoapp.exception.ErrorCode;
import com.neoapp.model.Usuario;
import com.neoapp.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponseDTO registrarNovoUsuario(RegisterRequestDTO registerRequest) {
        if (usuarioRepository.findByNome(registerRequest.nome()).isPresent()) {
            throw new BusinessRuleException(ErrorCode.USUARIO_ALREADY_EXISTS);
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(registerRequest.nome());
        novoUsuario.setSenha(passwordEncoder.encode(registerRequest.senha()));

        Usuario salvo = usuarioRepository.save(novoUsuario);
        return new UsuarioResponseDTO(salvo.getNome());
    }
}