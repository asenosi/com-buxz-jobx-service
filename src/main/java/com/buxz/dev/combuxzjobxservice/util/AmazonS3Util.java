package com.buxz.dev.combuxzjobxservice.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.util.FileUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AmazonS3Util {

    String accessKey = "accessKey";
    String secretKey = "secretKey";
    String region = "us-east-2";

    AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.US_EAST_2)
            .build();

    public void createS3Bucket(String bucketName) {
        if(s3client.doesBucketExistV2(bucketName)) {
            log.info("Bucket name : {} already exist. Try a different Bucket name", bucketName);
            return;
        }
        s3client.createBucket(bucketName);
    }

    public List<Bucket> getListOfS3Buckets() {
        try {
            List<Bucket> bucketList = s3client.listBuckets();
            for(Bucket bucket: bucketList) {
                log.info("Bucket: {}", bucket.getName());
            }
            return bucketList;
        } catch (Exception e) {
            log.error("Couldnt get list of bucket due : {}", e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    public void deleteS3Bucket(String bucketName) {
        try {
            s3client.deleteBucket(bucketName);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
        }
    }

    public void uploadToS3Bucket(String filename, File fileToUpload) {
        //TODO append filename with username to create S3 key /bucketname/username/filename
        String bucketName = "jobxstore";
        s3client.putObject(bucketName, filename, fileToUpload);
        log.info("File {} successfully uploaded to AmazonS3 from path {}", filename, fileToUpload.getAbsolutePath());
        FileUtil.deleteContents(fileToUpload);
        log.info("Deleting a file {} locally from {} after uploading", filename, fileToUpload.getAbsolutePath());
    }

    public void downloadFromS3() throws IOException {
        String bucketName = "";
        S3Object s3Object = s3client.getObject(bucketName, "");
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        FileUtil.copyStream(inputStream, new FileOutputStream("C/Users/user/Desktop/hello.txt\""));
    }

    public String deleteFileFromS3Bucket(String fileUrl) {
        String bucketName = "jobxstore";
        String filename = fileUrl.substring(fileUrl.lastIndexOf("/") +1);
        s3client.deleteObject(new DeleteObjectRequest(bucketName+ "/", filename));
        return "File deleted successfully";
    }
}
