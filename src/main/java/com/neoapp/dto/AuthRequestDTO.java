package com.neoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequestDTO(
        @NotBlank(message = "O campo 'username' não pode estar em branco")
        @Size(min = 4, max = 50)
        String username,

        @NotBlank(message = "O campo 'password' não pode estar em branco")
        @Size(min = 4, max = 100)
        String password) {
}
