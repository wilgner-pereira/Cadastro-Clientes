package com.neoapp.dto;

import com.neoapp.validation.Cpf;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ClienteRequestDTO(
        @NotBlank(message = "CPF é obrigatório")
        @Cpf
        String cpf,

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter no mínimo 2 caracteres e no máximo 100")
        @Pattern(regexp = "^[A-Za-zÀ-ú]+( [A-Za-zÀ-ú]+)*$", message = "Nome inválido")
        String nome,

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve estar no passado")
        LocalDate dataNascimento,

        @Email(message = "Email inválido")
        @NotBlank(message = "Email obrigatório")
        String email


){}
