package com.neoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRequestDTO(
        @NotBlank(message = "O campo 'username' não pode estar em branco")
        @Size(min = 4, max = 50)
        @Pattern(regexp = "^[A-Za-zÀ-ú]+( [A-Za-zÀ-ú]+)*$", message = "Nome inválido")
        String username,

        @NotBlank(message = "O campo 'password' não pode estar em branco")
        @Size(min = 4, max = 100)
        @Pattern(regexp = "^[A-Za-zÀ-ú0-9]+$", message = "Senha inválida")
        String password) {
}
