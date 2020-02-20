package com.allstars.recipie_management_system.service;

import com.allstars.recipie_management_system.entity.RecipeImage;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import java.util.Optional;

public interface RecipeImageService {

    String JPEG = "image/jpeg";
    String JPG = "image/jpg";
    String PNG = "image/png";

    AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1")
            .build();

    default boolean isImagePresent(MultipartFile imageFile) {
        if(imageFile == null) return false;
        return true;
    }

    default boolean isFileFormatRight(String fileMimeType) {
        if(fileMimeType.equals(JPEG) || fileMimeType.equals(JPG) || fileMimeType.equals(PNG)) return true;
        return false;
    }


    public RecipeImage uploadImage(MultipartFile multipartFile, String emailAddress, String recipeId, RecipeImage recipeImage) throws Exception;
    public String deleteImage(RecipeImage recipeImage, String recipeId) throws Exception;

}
