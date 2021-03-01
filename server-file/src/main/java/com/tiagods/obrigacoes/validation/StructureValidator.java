package com.tiagods.obrigacoes.validation;

import com.tiagods.obrigacoes.config.Regex;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StructureValidator implements ConstraintValidator<Structure,String> {

    @Autowired
    private Regex regex;

    @Override
    public void initialize(Structure constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.matches(regex.getStructure());
    }
}
