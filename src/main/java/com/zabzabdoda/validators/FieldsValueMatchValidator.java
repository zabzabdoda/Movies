package com.zabzabdoda.validators;

import com.zabzabdoda.annotations.FieldValuesMatch;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldsValueMatchValidator implements ConstraintValidator<FieldValuesMatch,Object> {

    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldValuesMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        Object fieldValue = new BeanWrapperImpl(o).getPropertyValue(field);
        Object fieldMatchValue = new BeanWrapperImpl(o).getPropertyValue(fieldMatch);
        if(fieldValue != null){
            return fieldValue.equals(fieldMatchValue);
        }else{
            return fieldMatchValue == null;
        }
    }
}
