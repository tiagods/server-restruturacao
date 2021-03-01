package com.tiagods.obrigacoes.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StructureValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Structure {
    String message() default "Parametro incorreto, nome valido (A a Z), 0 a 9, minimo de 3 caracteres e maximo 20";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
