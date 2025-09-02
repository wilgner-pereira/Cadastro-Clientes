
package com.neoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "O campo 'nome' não pode estar em branco")
        @Size(min = 4, max = 50)
        String nome,

        @NotBlank(message = "O campo 'senha' não pode estar em branco")
        @Size(min = 4, max = 50)
        String senha) {
}