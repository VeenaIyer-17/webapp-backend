package com.allstars.recipie_management_system.controller;


import com.allstars.recipie_management_system.dao.Userdao;
import com.allstars.recipie_management_system.entity.NutritionInformation;
import com.allstars.recipie_management_system.entity.OrderedList;
import com.allstars.recipie_management_system.entity.Recipie;
import com.allstars.recipie_management_system.entity.User;
import com.allstars.recipie_management_system.errors.RecipieCreationStatus;
import com.allstars.recipie_management_system.service.RecipieService;
import com.allstars.recipie_management_system.validators.RecipieValidator;
import org.hibernate.annotations.Synchronize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.*;

@RestController
public class RecipieController {

    @Autowired
    private RecipieService recipieService;

    @Autowired
    private RecipieValidator recipieValidator;

    @Autowired
    private Userdao userdao;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(recipieValidator);
    }

    private Recipie recipie;
    private Set<OrderedList> steps;
    private OrderedList oList;
    private NutritionInformation nInfo;
    private User user;

    @PostMapping(value = "v1/recipie")
    public ResponseEntity<?> createRecipie(@RequestHeader("Authorization") String token, @Valid @RequestBody Recipie recipie, BindingResult errors,
                                           HttpServletResponse response) throws Exception {
        RecipieCreationStatus recipieCreationStatus;

        if (errors.hasErrors()) {
            recipieCreationStatus = recipieService.getRecipieCreationStatus(errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    recipieCreationStatus);
        } else {
            String[] authDetails = decryptAuthenticationToken(token);
            User user = userdao.findByEmailId(authDetails[0]);
            Recipie newrecipie = recipieService.SaveRecipie(recipie, user);
            return new ResponseEntity<>(newrecipie, HttpStatus.CREATED);
        }
    }

    @GetMapping(value = "v1/recipie/{id}")
    public ResponseEntity<Recipie> getRecipe(@PathVariable("id") String id) {
        //System.out.println(recipeId);
        //UUID recipeId = UUID.fromString(id);
        Recipie recipe = recipieService.getRecipe(id);
        if (null != recipe) {
            return new ResponseEntity<Recipie>(recipe, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/v1/recipie/{id}")
    public ResponseEntity deleteRecipe(@PathVariable("id") String recipeId, @RequestHeader("Authorization") String token) throws UnsupportedEncodingException {
        String userDetails[] = decryptAuthenticationToken(token);
        Recipie existingRecipie = recipieService.getRecipe(recipeId);
        if (null != existingRecipie) {
            if (userdao.findByUuid(existingRecipie.getAuthor_id()).equals(userdao.findByEmailId(userDetails[0]))) {
                recipieService.deleteRecipe(recipeId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PutMapping(value = "v1/recipie/{recipieid}")
    public ResponseEntity<?> updateRecipie(@PathVariable("recipieid") String id, @RequestHeader("Authorization") String token, @Valid @RequestBody Recipie recipie, BindingResult errors,
                                           HttpServletResponse response) throws UnsupportedEncodingException {

        RecipieCreationStatus recipieCreationStatus;
        Recipie existingRecipe = recipieService.getRecipe(id);
        if (errors.hasErrors()) {
            recipieCreationStatus = recipieService.getRecipieCreationStatus(errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    recipieCreationStatus);
        } else {
            String[] authDetails = decryptAuthenticationToken(token);
            String userEmailID = authDetails[0];
            String t_id = id;
            if (existingRecipe == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
            } else {
                if (userdao.findByUuid(existingRecipe.getAuthor_id()).equals(userdao.findByEmailId(userEmailID))) {
                    recipieService.updateRecipe(recipie,existingRecipe);
                    return new ResponseEntity<Recipie>(recipie, HttpStatus.OK);
                } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
            }
        }
    }

    @GetMapping(value = "/v1/recipies")
    public ResponseEntity<?> getLatestRecipe() {
        long startTime = System.currentTimeMillis();
        Recipie recipe = null;
        if (recipieService.getLatestRecipie() != null) {
            recipe = recipieService.getLatestRecipie();
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);
            return new ResponseEntity<Recipie>(recipe, HttpStatus.OK);
        }
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public List<Recipie> retrieveAllRecipes() {
        List<Recipie> recipieList = new ArrayList<>();
        for (Recipie recipie : recipieService.getAllRecipes()) {
            recipieList.add(recipie);
        }
        return recipieList;
    }

    @GetMapping(value = "/v1/allrecipes")
    public ResponseEntity<Object> getAllRecipes() {
        return ResponseEntity.ok(retrieveAllRecipes());
    }

    @GetMapping(value = "/health")
    public ResponseEntity<Object> getHealthCheck() {
        HashMap<String, String> healthObject = new HashMap<>();
        healthObject.put("status", "up");
        return new ResponseEntity<>(healthObject, HttpStatus.OK);
    }

    public String[] decryptAuthenticationToken(String token) throws UnsupportedEncodingException {
        String[] basicAuthToken = token.split(" ");
        byte[] authKeys = Base64.getDecoder().decode(basicAuthToken[1]);
        return new String(authKeys, "utf-8").split(":");
    }

    @GetMapping(value = "/livenessCheck")
    @Cacheable(value = "liveness", sync = true)
    public Recipie createCache() throws Exception {

        List<String> ingredients = Arrays.asList(new String[]{"1", "abc", "some"});
        this.steps = new HashSet<>();
        this.oList = new OrderedList(1,"first");
        steps.add(oList);
        this.nInfo = new NutritionInformation(Integer.valueOf(2),Float.valueOf(1),Integer.valueOf(3),Float.valueOf(4),Float.valueOf(5));
        this.recipie = new Recipie(new Date(),new Date(),15,5,20,"samosa","indian",1,ingredients,steps,nInfo);
        recipie.setRecipeId("StringID246");
        this.user = new User("StringID246","ravi","kiran","kiranhun@gmail.com","WonderFul@28",new Date(),new Date());

        return recipie;

    }

}
