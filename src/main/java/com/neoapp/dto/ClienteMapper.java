package com.neoapp.dto;

import com.neoapp.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    // Converte DTO para entidade
    @Mapping(target = "cpf", expression = "java(dto.cpf().trim())")
    @Mapping(target = "nome", expression = "java(dto.nome().trim())")
    @Mapping(target = "email", expression = "java(dto.email().trim())")
    @Mapping(target = "id", ignore = true) // Ignora o ID na criação
    @Mapping(target = "createdAt", ignore = true) // Ignora o createdAt na criação
    Cliente toEntity(ClienteRequestDTO dto);

    ClienteResponseDTO toDto(Cliente cliente);

    @Mapping(target = "cpf", expression = "java(dto.cpf().trim())")
    @Mapping(target = "nome", expression = "java(dto.nome().trim())")
    @Mapping(target = "email", expression = "java(dto.email().trim())")
    @Mapping(target = "id", ignore = true) // Nunca atualize o ID da entidade
    @Mapping(target = "createdAt", ignore = true) // O createdAt também não deve ser alterado
    void updateClienteFromDto(ClienteRequestDTO dto, @MappingTarget Cliente cliente);
}
