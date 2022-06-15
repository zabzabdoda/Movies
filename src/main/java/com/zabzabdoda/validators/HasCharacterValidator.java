package com.zabzabdoda.validators;

import com.zabzabdoda.annotations.HasCharacter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HasCharacterValidator implements ConstraintValidator<HasCharacter,String> {
    @Override
    public void initialize(HasCharacter constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.matches(".*[a-zA-Z]*");
    }
}
