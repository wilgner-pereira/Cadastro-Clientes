package com.neoapp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<Cpf, String> {
    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context){
        // Checa se é null ou se não tem 11 dígitos numéricos
        if (cpf == null || !cpf.matches("\\d{11}")) return false;
        // Verifica se todos os dígitos são iguais
        if (cpf.chars().distinct().count() == 1) return false;

        int soma = 0;
        for(int i = 0; i < 9; i++){
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 -i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if(primeiroDigito >= 10) primeiroDigito = 0;

        soma = 0;
        for(int i = 0; i < 10; i++){
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 -i);
        }
        int segundoDigito = 11 - (soma % 11);
        if(segundoDigito >= 10) segundoDigito = 0;

        return primeiroDigito == Character.getNumericValue(cpf.charAt(9))
                && segundoDigito == Character.getNumericValue(cpf.charAt(10));

    }
}
