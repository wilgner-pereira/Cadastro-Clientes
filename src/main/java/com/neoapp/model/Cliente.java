package com.neoapp.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "clients")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "cpf", unique = true, length=11)
    private String cpf;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    private String email;

    private LocalDateTime createdAt;

    public void setId(Long id) {
        this.id = id;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getEmail() {
        return email;
    }

    public Cliente() {}
    public Cliente(String cpf, String nome, LocalDate dataNascimento, String email) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.email = email;

    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Cliente user = (Cliente) o;
        return Objects.equals(id, user.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
