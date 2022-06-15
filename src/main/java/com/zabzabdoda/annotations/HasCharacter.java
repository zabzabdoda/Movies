package com.zabzabdoda.annotations;

import com.zabzabdoda.validators.HasCharacterValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HasCharacterValidator.class)
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasCharacter {

    String message() default "Please include a character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
