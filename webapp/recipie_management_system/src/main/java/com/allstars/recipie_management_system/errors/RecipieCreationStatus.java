package com.allstars.recipie_management_system.errors;

public class RecipieCreationStatus {

    private String cookTimeError;

    private String prepTimeError;

    private String titlerror;

    private String cuisineError;

    private String servingsError;

    private String ingredientsError;

    private String stepsError;

    private String nutritionInformationError;

    public RecipieCreationStatus() {
        cookTimeError = "-";
        prepTimeError = "-";
        titlerror = "-";
        cuisineError = "-";
        ingredientsError = "-";
        stepsError = "-";
        nutritionInformationError = "-";
        servingsError = "-";
    }

    public RecipieCreationStatus(String cookTimeError, String prepTimeError, String titlerror, String cuisineError, String servingsError, String ingredientsError, String stepsError, String nutritionInformationError) {
        this.cookTimeError = cookTimeError;
        this.prepTimeError = prepTimeError;
        this.titlerror = titlerror;
        this.cuisineError = cuisineError;
        this.servingsError = servingsError;
        this.ingredientsError = ingredientsError;
        this.stepsError = stepsError;
        this.nutritionInformationError = nutritionInformationError;
    }

    public String getCookTimeError() {
        return cookTimeError;
    }

    public void setCookTimeError(String cookTimeError) {
        this.cookTimeError = cookTimeError;
    }

    public String getPrepTimeError() {
        return prepTimeError;
    }

    public void setPrepTimeError(String prepTimeError) {
        this.prepTimeError = prepTimeError;
    }

    public String getTitlerror() {
        return titlerror;
    }

    public void setTitlerror(String titlerror) {
        this.titlerror = titlerror;
    }

    public String getCuisineError() {
        return cuisineError;
    }

    public void setCuisineError(String cuisineError) {
        this.cuisineError = cuisineError;
    }

    public String getServingsError() {
        return servingsError;
    }

    public void setServingsError(String servingsError) {
        this.servingsError = servingsError;
    }

    public String getIngredientsError() {
        return ingredientsError;
    }

    public void setIngredientsError(String ingredientsError) {
        this.ingredientsError = ingredientsError;
    }

    public String getStepsError() {
        return stepsError;
    }

    public void setStepsError(String stepsError) {
        this.stepsError = stepsError;
    }

    public String getNutritionInformationError() {
        return nutritionInformationError;
    }

    public void setNutritionInformationError(String nutritionInformationError) {
        this.nutritionInformationError = nutritionInformationError;
    }
}
