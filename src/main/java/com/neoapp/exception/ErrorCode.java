package com.neoapp.exception;

public enum ErrorCode {
    // NÃO ENCONTRADO OU CONFLITO
    CLIENTE_NOT_FOUND("Cliente não encontrado."),
    CPF_ALREADY_EXISTS("Já existe cadastro com este CPF."),
    EMAIL_ALREADY_EXISTS("Já existe cadastro com este E-mail."),
    USUARIO_ALREADY_EXISTS("Já existe cadastro com este nome."),
    USUARIO_NOT_FOUND("Usuário não encontrado."),

    // REGRA DE NEGOCIO
    VALIDATION_ERROR("Erro de validação."),
    UNAUTHORIZED_ACTION("Ação não autorizada."),
    INVALID_INPUT("Erro na validação dos campos"),
    BAD_FORMAT_JSON("JSON inválido ou malformado"),
    UNEXPECTED_ERROR("Erro inesperado");

    private final String mensagem;

    ErrorCode(String mensagem) {
        this.mensagem = mensagem;
    }
    public String getMensagem() {
        return mensagem;
    }
}
