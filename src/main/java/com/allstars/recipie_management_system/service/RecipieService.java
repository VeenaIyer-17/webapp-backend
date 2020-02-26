package com.allstars.recipie_management_system.service;

import com.allstars.recipie_management_system.dao.RecipieDao;
import com.allstars.recipie_management_system.entity.Recipie;
import com.allstars.recipie_management_system.entity.User;
import com.allstars.recipie_management_system.errors.RecipieCreationStatus;
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

@Service
public class RecipieService {

    private final RecipieDao recipieDao;

    public RecipieService(RecipieDao recipieDao) {
        this.recipieDao = recipieDao;
    }

    @Caching(put = {@CachePut(value = "recipie", key = "#recipie.recipeId")}, evict = {@CacheEvict(value = "allRecipes", allEntries = true)})
    public Recipie SaveRecipie(Recipie recipie, User user) {
        recipie.setUser(user);
        recipie.setAuthor_id(user.getUuid());
        recipie.setCreatedts(new Date());
        recipie.setUpdated_ts();
        recipie.setTotal_time_in_min();
        recipie = recipieDao.save(recipie);
        return recipie;
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

    @Cacheable(value = "recipie", key = "#recipeId",condition="#fetchFromCache")
    public Recipie getRecipe(String recipeid) {
        return recipieDao.findByRecipeid(recipeid);
    }

    @Caching(evict = {@CacheEvict(value = "recipie"), @CacheEvict(value = "allRecipes", allEntries = true)})
    public void deleteRecipe(String recipeId) {
        recipieDao.deleteById(recipeId);
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
                recipieDao.save(recipie);
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

    @Cacheable(value = "allRecipes",condition="#fetchFromCache")
    public List<Recipie> getAllRecipes() {
        return recipieDao.findAll();
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
