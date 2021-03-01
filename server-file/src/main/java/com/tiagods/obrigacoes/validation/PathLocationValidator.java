package com.tiagods.obrigacoes.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathLocationValidator implements ConstraintValidator<PathLocation,String> {
    @Override
    public void initialize(PathLocation constraintAnnotation) {
    }
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Path path = Paths.get(value);
        return Files.exists(path) && Files.isDirectory(path);
    }
}
