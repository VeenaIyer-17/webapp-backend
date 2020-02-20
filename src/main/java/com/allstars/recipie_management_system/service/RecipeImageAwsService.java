package com.allstars.recipie_management_system.service;

import com.allstars.recipie_management_system.entity.RecipeImage;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class RecipeImageAwsService implements RecipeImageService {


    private final static Logger logger = LoggerFactory.getLogger(RecipeImageService.class);

    private AmazonS3 s3client;

    private String dir = "Images";

    @Value("${amazonProperties.bucketName}")
    private String bucketName;


    @PostConstruct
    private void initializeAmazon() {
        this.s3client = AmazonS3ClientBuilder.standard().withRegion("us-east-1").withCredentials(new EnvironmentVariableCredentialsProvider()).build();
    }

    @Override
    public RecipeImage uploadImage(MultipartFile multipartFile, String fileName, String recipeId, RecipeImage recipeImage) throws Exception {

        String name = this.dir + "/" + recipeId + "/" + fileName;

        logger.info(name);

        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

        long startTime = System.currentTimeMillis();
        PutObjectResult data = s3client.putObject(bucketName, name, multipartFile.getInputStream(), new ObjectMetadata());
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        String fileUrl = getPreSignedURL(name, bucketName);

        recipeImage.setUrl(fileUrl);
        recipeImage.setMd5Hash(data.getContentMd5());

        return recipeImage;
    }

    public String deleteImage(RecipeImage recipeImage, String recipeId) throws Exception {
        String fileUrl = recipeImage.getUrl();
        String fileName = "Images/" + recipeId + "/" + fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        long startTime = System.currentTimeMillis();
        for (S3ObjectSummary file : s3client.listObjects(bucketName, fileName).getObjectSummaries()) {
            s3client.deleteObject(bucketName, file.getKey());
        }
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        logger.info("Image deleted Successfully");
        return "Successfully deleted";
    }

    public String getPreSignedURL(String objKey, String bucketName) {
        URL url = null;
        try {

            // Set the presigned URL to expire after 2min.
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 2;
            expiration.setTime(expTimeMillis);
            // Generate the presigned URL.
            System.out.println("Generating pre-signed URL.");
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objKey).withMethod(HttpMethod.GET).withExpiration(expiration);
            url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
            System.out.println("Pre-Signed URL: " + url.toString());
        } catch (SdkClientException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        }   // Amazon S3 couldn't be contacted for a response, or the client
        // couldn't parse the response from Amazon S3.
        assert url != null;
        return url.toString();
    }

}
