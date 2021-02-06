package pkg.andres.test;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AwsS3Getter {

    private static final Regions S3_REGION = Regions.US_EAST_1;

    private static AwsS3Getter instance = null;

    private AmazonS3 client;

    private AwsS3Getter(BasicAWSCredentials awsCreds) {
        
        client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
            .withRegion(S3_REGION)
            .build();
    }

    public S3Object getObject(String bucketName, String objectKey){
        
        S3Object object = null;
        
        try{
            object = client.getObject(bucketName, objectKey);
        } catch ( AmazonServiceException ase) {
            ase.printStackTrace();
        }

        return object;
    }

    public List<String> getFileNames(String bucketName){
        List<String> filenames = new ArrayList<String>();

        ListObjectsV2Result result = client.listObjectsV2(bucketName);
        for(S3ObjectSummary obj: result.getObjectSummaries()){
            filenames.add(obj.getKey());
        }

        return filenames;
    }

    public static AwsS3Getter getInstance(BasicAWSCredentials awsCreds){
        if(instance == null){
            instance = new AwsS3Getter(awsCreds);
        }
        return instance;
    }
}
