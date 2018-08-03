package com.imooc.miaosha.validator;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (required){
            if (value.length()==11){
                return true;
            }else {
                return false;
            }
        }else {
            if (StringUtils.isEmpty(value)){
                return false;
            }else if (value.length()==11){
                return true;
            }else {
                return false;
            }
        }
    }
}
