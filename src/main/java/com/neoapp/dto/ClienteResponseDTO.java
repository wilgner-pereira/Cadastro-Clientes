package com.neoapp.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public record ClienteResponseDTO(
        Long id,
        String cpf,
        String nome,
        LocalDate dataNascimento,
        String email,
        LocalDateTime createdAt

)   {
        @JsonProperty
        public int idade() {
            return Period.between(this.dataNascimento, LocalDate.now()).getYears();
        }
    }


