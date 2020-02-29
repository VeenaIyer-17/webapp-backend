package com.allstars.recipie_management_system.dao;

import com.allstars.recipie_management_system.entity.Recipie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipieDao extends CrudRepository<Recipie, String> {

    Recipie findByRecipeid(String id);
    Recipie findTopByOrderByCreatedtsDesc();
    List<Recipie> findAll();

}
