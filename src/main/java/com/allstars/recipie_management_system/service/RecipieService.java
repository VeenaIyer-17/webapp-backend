package com.allstars.recipie_management_system.service;

import com.allstars.recipie_management_system.dao.RecipieDao;
import com.allstars.recipie_management_system.entity.Recipie;
import com.allstars.recipie_management_system.entity.User;
import com.allstars.recipie_management_system.errors.RecipieCreationStatus;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/*Storing the cache in redis with ttl of 2 minutes. The key @CachePut always calls the method so it updates the cache for put and post methods where as @Cacheable only
when it doesn't have that key-value pair */
@Service
public class RecipieService {

    private final RecipieDao recipieDao;

    @Autowired
    MeterRegistry registry;

    Timer recipeTimer;

    public RecipieService(RecipieDao recipieDao) {
        this.recipieDao = recipieDao;
    }

    @CachePut(value = "recipe", key = "#recipie.recipeId")
    public Recipie SaveRecipie(Recipie recipie, User user) {
        recipeTimer = registry.timer("custom.metrics.timer", "Backend", "RecipeSAVE");

        recipie.setUser(user);
        recipie.setAuthor_id(user.getUuid());
        recipie.setCreatedts(new Date());
        recipie.setUpdated_ts();
        recipie.setTotal_time_in_min();

        final Recipie[] recipeEntities = new Recipie[1];
        recipeTimer.record(() -> recipeEntities[0] = recipieDao.save(recipie));
        return recipeEntities[0];
    }


    public RecipieCreationStatus getRecipieCreationStatus(BindingResult errors) {
        FieldError cookTimeError = errors.getFieldError("cook_time_in_min");
        FieldError prepTimeError = errors.getFieldError("prep_time_in_min");
        FieldError titleError = errors.getFieldError("title");
        FieldError cuisineError = errors.getFieldError("cuisine");
        FieldError ingredientsError = errors.getFieldError("ingredients");
        FieldError stepsError = errors.getFieldError("steps");
        FieldError nutritionInformationError = errors.getFieldError("nutritionInformation");
        FieldError servingsError = errors.getFieldError("servings");

        String cookTimeErrorMessage = cookTimeError == null ? "-" : cookTimeError.getCode();
        String prepTimeErrorMessage = prepTimeError == null ? "-" : prepTimeError.getCode();
        String titleErrorMessage = titleError == null ? "-" : titleError.getCode();
        String cuisineErrorMessage = cuisineError == null ? "-" : cuisineError.getCode();
        String ingredientsErrorMessage = ingredientsError == null ? "-" : ingredientsError.getCode();
        String stepsErrorMessage = stepsError == null ? "-" : stepsError.getCode();
        String nutritionInformationErrorMessage = nutritionInformationError == null ? "-" : nutritionInformationError.getCode();
        String servingsErrorMessage = servingsError == null ? "-" : servingsError.getCode();
        RecipieCreationStatus recipieCreationStatus = new RecipieCreationStatus(cookTimeErrorMessage, prepTimeErrorMessage, titleErrorMessage, cuisineErrorMessage, servingsErrorMessage, ingredientsErrorMessage, stepsErrorMessage, nutritionInformationErrorMessage);
        return recipieCreationStatus;
    }

    @Cacheable(value = "recipe", key = "#recipeid", unless = "#result == null")
    public Recipie getRecipe(String recipeid) {
        recipeTimer = registry.timer("custom.metrics.timer", "Backend", "RecipeGET");
        final Recipie[] recipeEntities = new Recipie[1];
        recipeTimer.record(() -> recipeEntities[0] =  recipieDao.findByRecipeid(recipeid));
        return recipeEntities[0];
    }

    @Caching(evict = {@CacheEvict(key = "#recipeId", value = "recipe")})
    public void deleteRecipe(String recipeId) {
        recipeTimer = registry.timer("custom.metrics.timer", "Backend", "RecipeDELETE");
        recipeTimer.record(() -> recipieDao.deleteById(recipeId));
    }

    @CachePut(value = "recipe", key = "#existingRecipe.recipeId")
    public Recipie updateRecipe(Recipie updateRecipe, Recipie existingRecipe) {
        updateRecipe.setRecipeId(existingRecipe.getRecipeId());
        updateRecipe.setUser(existingRecipe.getUser());
        updateRecipe.setAuthor_id(existingRecipe.getAuthor_id());
        updateRecipe.setCreatedts(existingRecipe.getCreatedts());
        updateRecipe.setUpdated_ts();
        updateRecipe.setTotal_time_in_min();

        recipeTimer = registry.timer("custom.metrics.timer", "Backend", "RecipeUPDATE");
        final Recipie[] recipeEntities = new Recipie[1];
        recipeTimer.record(() -> recipeEntities[0] =  recipieDao.save(updateRecipe));
        return recipeEntities[0];
    }

    public ResponseEntity<?> updateRecipie(String id, String userEmailId, Recipie recipie) {

        Recipie retrivedRecipie = recipieDao.findByRecipeid(id);

        if (retrivedRecipie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        } else {
            if (retrivedRecipie.getUser().getEmailId().equals(userEmailId)) {

                recipie.setRecipeId(retrivedRecipie.getRecipeId());
                recipie.setUser(retrivedRecipie.getUser());
                recipie.setAuthor_id(retrivedRecipie.getAuthor_id());
                recipie.setCreatedts(retrivedRecipie.getCreatedts());
                recipie.setUpdated_ts();
                recipie.setTotal_time_in_min();
                this.SaveRecipie(recipie, recipie.getUser());
                return new ResponseEntity<Recipie>(recipie, HttpStatus.CREATED);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
            }
        }
    }

    public Recipie getLatestRecipie() {
        try {
            Recipie latestRecipe = null;
            if (recipieDao.findTopByOrderByCreatedtsDesc() != null) {
                latestRecipe = recipieDao.findTopByOrderByCreatedtsDesc();
            }
            return latestRecipe;
        } catch (Exception exc) {
            return null;
        }
    }

    public List<Recipie> getAllRecipes() {
        recipeTimer = registry.timer("custom.metrics.timer", "Backend", "RecipeLIST");
        final List<Recipie>[] recipeEntities = new List[1];
        recipeTimer.record(() -> recipeEntities[0] = recipieDao.findAll());
        return  recipeEntities[0];
    }

    public Optional<Recipie> findById(String idRecipe) {
        try {
            return recipieDao.findById(idRecipe);
        } catch (Exception exc) {
            return null;
        }
    }

    public boolean isRecipeImagePresent(Recipie recipie) {
        if (recipie.getImage() == null) return false;
        return true;
    }
}
