package com.neoapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ClienteRequestDTO(
        @NotBlank(message = "CPF é obrigatório")
        @Size(min = 11, max = 11, message = "CPF deve ter 11 dígitos")
        String cpf,

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nome,

        @NotNull(message = "Data de nascimento é obrigatória")
        LocalDate dataNascimento,

        @Email(message = "Email inválido")
        String email


){}
