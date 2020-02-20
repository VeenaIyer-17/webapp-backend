package com.allstars.recipie_management_system.dao;

import com.allstars.recipie_management_system.entity.RecipeImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeImageDao extends CrudRepository<RecipeImage, String> {
    RecipeImage findByImageId(String Imageid);
}
