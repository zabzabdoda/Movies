package com.zabzabdoda.annotations;

import com.zabzabdoda.validators.HasCharacterValidator;
import com.zabzabdoda.validators.HasDigitValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HasDigitValidator.class)
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasDigit {

    String message() default "Please include a digit";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
