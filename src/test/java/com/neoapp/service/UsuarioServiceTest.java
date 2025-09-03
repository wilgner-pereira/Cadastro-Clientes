package com.neoapp.service;

import com.neoapp.dto.RegisterRequestDTO;
import com.neoapp.dto.UsuarioResponseDTO;
import com.neoapp.exception.BusinessRuleException;
import com.neoapp.exception.ErrorCode;
import com.neoapp.model.Usuario;
import com.neoapp.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    @InjectMocks
    private UsuarioServiceImpl service;

    private RegisterRequestDTO createDTO;
    private Usuario user;

    @BeforeEach
    void setUp() {
        createDTO = new RegisterRequestDTO("wilgner", "cadeira120");
        user = new Usuario();
        user.setId(1L);
        user.setUsername(createDTO.username());
        user.setPassword("passwordCriptografrado");
    }

    @Test
    void createUser_whenUsernameNotExists_thenReturnResponseDTO(){
        when(userRepository.findByUsername(createDTO.username())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(createDTO.password())).thenReturn("passwordCriptografrado");
        when(userRepository.save(usuarioCaptor.capture())).thenReturn(user);

        UsuarioResponseDTO result = service.registrarNovoUsuario(createDTO);

        assertNotNull(result);
        assertEquals(createDTO.username(), result.username());

        Usuario userCaptured = usuarioCaptor.getValue();
        assertEquals(createDTO.username(), userCaptured.getUsername());
        assertEquals("passwordCriptografrado", userCaptured.getPassword());

        verify(userRepository).findByUsername(createDTO.username());
        verify(bCryptPasswordEncoder).encode(createDTO.password());
        verify(userRepository).save(usuarioCaptor.capture());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUser_whenUsernameExists_thenReturnBusinessRuleException(){
        when(userRepository.findByUsername(createDTO.username())).thenReturn(Optional.of(user));

        BusinessRuleException thrown = assertThrows(BusinessRuleException.class, () -> service.registrarNovoUsuario(createDTO));

        assertEquals(ErrorCode.USUARIO_ALREADY_EXISTS, thrown.getErrorCode());

        verify(userRepository).findByUsername(createDTO.username());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(bCryptPasswordEncoder);
    }

}
