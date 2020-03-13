package com.allstars.recipie_management_system.controller;

import com.allstars.recipie_management_system.dao.RecipeImageDao;
import com.allstars.recipie_management_system.dao.RecipieDao;
import com.allstars.recipie_management_system.entity.RecipeImage;
import com.allstars.recipie_management_system.entity.Recipie;
import com.allstars.recipie_management_system.service.RecipeImageService;
import com.allstars.recipie_management_system.service.RecipieService;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/v1/recipie/{idRecipe}/*")
public class RecipeImageController {


    @Autowired
    private RecipieService recipieService;


    @Autowired
    private RecipeImageService recipeImageService;


    @Autowired
    RecipieDao recipieDao;

    @Autowired
    RecipeImageDao recipeImageDao;

    @Autowired
    MeterRegistry registry;

    Timer imageTimer;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    private final static Logger logger = LoggerFactory.getLogger(RecipeImageController.class);

    @RequestMapping(method = RequestMethod.POST, value = "image")
    public ResponseEntity<?> addRecipeImage(@PathVariable String idRecipe, @RequestParam MultipartFile image, HttpServletRequest request,@RequestHeader("Authorization") String token) throws Exception {
        registry.counter("custom.metrics.counter", "ApiCall", "ImagePost").increment();
        log.info("Inside post /recipe/id/ mapping");

        long startTime = System.currentTimeMillis();
        if (!recipeImageService.isImagePresent(image)) {
            logger.error("Post image failed. Please select an image file");
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"error\": \"Select a file\" }");
        }
        if (!recipeImageService.isFileFormatRight(image.getContentType())) {
            logger.error("Post image failed. Image format must be .jpg, .jpeg or .png");
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"error\": \"Image File Format Wrong\" }");
        }
        Recipie recipie = recipieDao.findByRecipeid(idRecipe);

        String userDetails[] = decryptAuthenticationToken(token);

        if (recipie != null) {
            logger.error("My crecipe"+recipie);
            if(recipieService.isRecipeImagePresent(recipie)) {
                logger.error("POST->Cover exist already perform PUT to modify");
                long endTime = System.currentTimeMillis();
                long duration = (endTime - startTime);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"error\": \"POST->Recipe Image exist already perform PUT to modify\" }");
            }

            if (recipie.getUser().getEmailId().equalsIgnoreCase(userDetails[0])){

                RecipeImage recipeImage = new RecipeImage();
                String photoNewName = image.getOriginalFilename();
                recipeImage = recipeImageService.uploadImage(image, photoNewName,recipie.getRecipeId(),recipeImage);
                recipie.setImage(recipeImage);
                Recipie rec = recipieService.updateRecipe(recipie,recipie);
                RecipeImage recImg = rec.getImage();
                logger.info("Image Posted succcessfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(recImg);

            }
            else{
                logger.error("User does not exist");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
            }
        }
        else {
            logger.error("Delete recipe failed. Recipe not found.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"error\": \"Recipe not Found\" }");
        }
    }


    @DeleteMapping("/image/{idImage}")
    public ResponseEntity<?> deleteRecipeImage(@PathVariable String idRecipe, @PathVariable String idImage,@RequestHeader("Authorization") String token) throws Exception {
        registry.counter("custom.metrics.counter", "ApiCall", "ImageDelete").increment();
        imageTimer = registry.timer("custom.metrics.timer", "Backend", "ImageDELETE");
        log.info("Inside delete /recipe/image/id mapping");
        long startTime = System.currentTimeMillis();
        String userDetails[] = decryptAuthenticationToken(token);

        Recipie recipie = recipieDao.findByRecipeid(idRecipe);

        if (recipie != null) {

            if (recipie.getUser().getEmailId().equalsIgnoreCase(userDetails[0])){

                RecipeImage recipeImgOfPassedrecipe = recipie.getImage();
                if (recipeImgOfPassedrecipe != null){
                    RecipeImage recipeImage = recipeImageDao.findByImageId(idImage);
                    if (recipeImage != null){
                        if (recipeImgOfPassedrecipe.getImageId().equals(recipeImage.getImageId())){
                            if (recipeImage != null) {
                                recipeImageService.deleteImage(recipeImage,recipie.getRecipeId());
                                recipie.setImage(null);

                                imageTimer.record(() -> recipeImageDao.delete(recipeImage));

                                recipieService.updateRecipe(recipie,recipie);
                                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
                            }
                            else {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
                            }
                        }
                        else {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
                        }
                    }
                    else{
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
                    }
                }
                else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
                }

            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
            }

        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    @GetMapping("/image/{idImage}")
    public ResponseEntity<?> getImage(@PathVariable String idRecipe, @PathVariable String idImage) throws Exception {
        registry.counter("custom.metrics.counter", "ApiCall", "ImageGet").increment();
        log.info("Inside get /recipe/id/image/id mapping");
        long startTime = System.currentTimeMillis();
        Recipie recipie = recipieDao.findByRecipeid(idRecipe);
        if (recipie != null) {
            imageTimer = registry.timer("custom.metrics.timer", "Backend", "ImageGET");
            final RecipeImage[] imageEntities = new RecipeImage[1];
            imageTimer.record(() -> imageEntities[0] =  recipeImageDao.findByImageId(idImage));
            //return imageEntities[0];

            //RecipeImage recipeImage = recipeImageDao.findByImageId(idImage);
            if (imageEntities[0] == null) {
                logger.error("Get image failed. Recipe not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
            }
            else {
                if (recipie.getImage().getImageId().equals(imageEntities[0].getImageId())){
                    logger.info("Image get successful");
                    return ResponseEntity.status(HttpStatus.OK).body(imageEntities[0]);
                }
                else{
                    logger.error("The image specified does not exist for this recipie");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
                }

            }

        }
        else {
            logger.error("Get image failed. Recipe doesnt exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    public String[] decryptAuthenticationToken(String token) throws UnsupportedEncodingException {
        String[] basicAuthToken = token.split(" ");
        byte[] authKeys = Base64.getDecoder().decode(basicAuthToken[1]);
        return new String(authKeys,"utf-8").split(":");
    }
}

