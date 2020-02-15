package com.allstars.recipie_management_system.validators;

import com.allstars.recipie_management_system.entity.OrderedList;
import com.allstars.recipie_management_system.entity.Recipie;
import com.allstars.recipie_management_system.service.RecipieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RecipieValidator implements Validator {

    @Autowired
    private RecipieService recipieService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Recipie.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cook_time_in_min", "cook_time_in_min required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "prep_time_in_min", "prep_time_in_min required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "title required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cuisine", "cuisine required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ingredients", "ingredients required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "steps", "steps required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nutritionInformation", "nutritionInformation required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "servings", "servings required");


        if(errors.hasErrors()) return;

        Recipie recipie = (Recipie) o;
        String nutritionError = "";
        if(recipie.getNutritionInformation().getCalories() == null){
            nutritionError = nutritionError + "Calories cannot be empty";
        }

        if(recipie.getNutritionInformation().getCholesterol_in_mg() == null){
            nutritionError = nutritionError + " Cholesterol cannot be empty";
        }

        if(recipie.getNutritionInformation().getSodium_in_mg() == null){
            nutritionError = nutritionError + " sodium cannot be empty";
        }

        if(recipie.getNutritionInformation().getCarbohydrates_in_grams() == null){
            nutritionError = nutritionError + " Carbohydrates cannot be empty";
        }

        if(recipie.getNutritionInformation().getProtein_in_grams() == null){
            nutritionError = nutritionError + " Proteins cannot be empty";
        }
        if(!nutritionError.isEmpty()){
            errors.rejectValue("nutritionInformation", nutritionError);
        }

        if(recipie.getCook_time_in_min() == 0){
            errors.rejectValue("cook_time_in_min", "Cook time cannot be 0");
        }

        if(recipie.getPrep_time_in_min() == 0){
            errors.rejectValue("prep_time_in_min", "Prep time cannot be 0");
        }

        for(OrderedList ol : recipie.getSteps()){
            if(ol.getPosition() == null || ol.getPosition() < 1){
                errors.rejectValue("steps", "Position in steps cannot be null and has to be >= 1 ");
            }
        }

        if(!((recipie.getServings() >=1) && (recipie.getServings() <= 5)) ){
            errors.rejectValue("servings", "Servings must be between 1 and 5");
        }
        if(((recipie.getCook_time_in_min())%5 != 0)){
            errors.rejectValue("cook_time_in_min", "Cook Time should be a multiple of 5");
        }
        if((recipie.getPrep_time_in_min() % 5 != 0)){
            errors.rejectValue("prep_time_in_min", "Prep Time should be a multiple of 5");
        }

        if(errors.hasErrors()) return;
    }

}
