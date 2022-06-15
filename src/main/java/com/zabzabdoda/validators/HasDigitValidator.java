package com.zabzabdoda.validators;

import com.zabzabdoda.annotations.HasDigit;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HasDigitValidator implements ConstraintValidator<HasDigit,String> {
    @Override
    public void initialize(HasDigit constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.matches(".*[0-9]*");
    }
}
