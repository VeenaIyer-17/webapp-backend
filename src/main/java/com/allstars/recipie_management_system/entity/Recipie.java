package com.allstars.recipie_management_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@JsonIgnoreProperties(value={"user"}, allowSetters= true)
public class Recipie {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "recipeid", columnDefinition = "CHAR(32)")
    @Id
    private String recipeid;

    @Column
    private Date createdts;

    @Column
    private Date updated_ts;

    @ManyToOne(optional = true)
    @JoinColumn(name="userid", nullable = true)
    private User user;

    @Column
    private String authorid;

    @Column
    private Integer cook_time_in_min;

    @Column
    private Integer prep_time_in_min;

    @Column
    private int total_time_in_min;

    @Column
    private String title;

    @Column
    private String cuisine;

    @Column
    private Integer servings;

    @Column
    @ElementCollection
    private List<String> ingredients;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name="recipie_id")
    private Set<OrderedList> steps;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="NIid")
    private NutritionInformation nutritionInformation;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private RecipeImage image;

    public Recipie(Date created_ts, Date updated_ts, Integer cook_time_in_min, Integer prep_time_in_min, int total_time_in_min, String title, String cuisine, Integer servings, List<String> ingredients, Set<OrderedList> steps, NutritionInformation nutritionInformation) {
        //this.created_ts = created_ts;
        this.updated_ts = updated_ts;
        this.cook_time_in_min = cook_time_in_min;
        this.prep_time_in_min = prep_time_in_min;
        //this.total_time_in_min = total_time_in_min;
        this.title = title;
        this.cuisine = cuisine;
        this.servings = servings;
        this.ingredients = ingredients;
        this.steps = steps;
        this.nutritionInformation = nutritionInformation;
    }

    public Recipie() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAuthor_id() {
        return authorid;
    }

    public void setAuthor_id(String author_id) {
        this.authorid = author_id;
    }

    public String getRecipeId() {
        return recipeid;
    }

    public void setRecipeId(String recipeId) {
        this.recipeid = recipeId;
    }

    public Date getCreatedts() {
        return createdts;
    }

    public void setCreatedts(Date created_ts) {
        this.createdts = created_ts;
    }

    public Date getUpdated_ts() {
        return updated_ts;
    }

    public void setUpdated_ts() {

        this.updated_ts = new Date();

    }

    public Integer getCook_time_in_min() {
        return cook_time_in_min;
    }

    public void setCook_time_in_min(Integer cook_time_in_min) {
        this.cook_time_in_min = cook_time_in_min;
    }

    public Integer getPrep_time_in_min() {
        return prep_time_in_min;
    }

    public void setPrep_time_in_min(Integer prep_time_in_min) {
        this.prep_time_in_min = prep_time_in_min;
    }

    public int getTotal_time_in_min() {
        return total_time_in_min;
    }

    public void setTotal_time_in_min() {
        this.total_time_in_min = this.cook_time_in_min + this.prep_time_in_min;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public Set<OrderedList> getSteps() {
        return steps;
    }

    public void setSteps(Set<OrderedList> steps) {
        this.steps = steps;
    }

    public NutritionInformation getNutritionInformation() {
        return nutritionInformation;
    }

    public void setNutritionInformation(NutritionInformation nutritionInformation) {
        this.nutritionInformation = nutritionInformation;
    }

    public RecipeImage getImage() {
        return image;
    }

    public void setImage(RecipeImage image) {
        this.image = image;
    }
}

