package com.allstars.recipie_management_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@JsonIgnoreProperties(value={"NuInfoID"}, allowSetters= true)
public class NutritionInformation {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uuid", columnDefinition = "CHAR(32)")
    @Id
    private String NuInfoID;

    @Column(nullable=false)
    private Integer calories;

    @Column(nullable=false)
    private Float cholesterol_in_mg;

    @Column(nullable=false)
    private Integer sodium_in_mg;

    @Column(nullable=false)
    private Float carbohydrates_in_grams;

    @Column(nullable=false)
    private Float protein_in_grams;


    public NutritionInformation(Integer calories, Float cholesterol_in_mg, Integer sodium_in_mg, Float carbohydrates_in_grams, Float protein_in_grams) {
        this.calories = calories;
        this.cholesterol_in_mg = cholesterol_in_mg;
        this.sodium_in_mg = sodium_in_mg;
        this.carbohydrates_in_grams = carbohydrates_in_grams;
        this.protein_in_grams = protein_in_grams;
    }

    public NutritionInformation() {
    }

    @JsonIgnore
    public String getNuInfoID() {
        return NuInfoID;
    }

    @JsonProperty("id")
    public void setNuInfoID(String nuInfoID) {
        NuInfoID = nuInfoID;
    }

    public Integer getCalories() {

        return calories;

    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Float getCholesterol_in_mg() {
        return cholesterol_in_mg;
    }

    public void setCholesterol_in_mg(Float cholesterol_in_mg) {
        this.cholesterol_in_mg = cholesterol_in_mg;
    }

    public Integer getSodium_in_mg() {
        return sodium_in_mg;
    }

    public void setSodium_in_mg(Integer sodium_in_mg) {
        this.sodium_in_mg = sodium_in_mg;
    }

    public Float getCarbohydrates_in_grams() {
        return carbohydrates_in_grams;
    }

    public void setCarbohydrates_in_grams(Float carbohydrates_in_grams) {
        this.carbohydrates_in_grams = carbohydrates_in_grams;
    }

    public Float getProtein_in_grams() {
        return protein_in_grams;
    }

    public void setProtein_in_grams(Float protein_in_grams) {
        this.protein_in_grams = protein_in_grams;
    }
}


