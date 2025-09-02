package com.neoapp;

import com.neoapp.model.Usuario;
import com.neoapp.repository.UsuarioRepository;
import com.neoapp.security.AppUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AppUserDetailsService userDetailsService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("wilgner");
        usuario.setPassword("senha123");
    }

    @Test
    void loadUserByUsername_usuarioExiste_retornaUserDetails() {
        when(usuarioRepository.findByUsername("wilgner")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("wilgner");

        assertNotNull(userDetails);
        assertEquals(usuario.getUsername(), userDetails.getUsername());
        assertEquals(usuario.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(usuarioRepository).findByUsername("wilgner");
    }

    @Test
    void loadUserByUsername_usuarioNaoExiste_lancaException() {
        when(usuarioRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("inexistente"));

        assertEquals("Usuário não encontrado", ex.getMessage());
        verify(usuarioRepository).findByUsername("inexistente");
    }
}
