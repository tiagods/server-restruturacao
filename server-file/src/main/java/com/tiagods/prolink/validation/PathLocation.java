package com.tiagods.prolink.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PathLocationValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PathLocation {
    String message() default "O diretorio informado não existe";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
