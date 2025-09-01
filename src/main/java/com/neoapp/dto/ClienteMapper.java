package com.neoapp.dto;

import com.neoapp.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    Cliente toEntity(ClienteRequestDTO dto);

    @Mapping(target = "idade", expression = "java(calcularIdade(cliente.getDataNascimento()))")
    ClienteResponseDTO toDto(Cliente cliente);

    default int calcularIdade(LocalDate dataNascimento) {
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }
}
