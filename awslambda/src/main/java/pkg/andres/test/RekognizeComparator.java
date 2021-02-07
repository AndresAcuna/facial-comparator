package pkg.andres.test;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;

class RekognizeComparator {

    private static RekognizeComparator instance;

    private Map<PairedKey, Float> similarityScores;

    AmazonRekognition client;

    private RekognizeComparator( ){
        client = AmazonRekognitionClientBuilder
            .standard()
            .build();
        similarityScores = new HashMap<PairedKey, Float>();
    }

    public void compare(String imageNameA, String imageNameB, String bucketName){

        Image imageA = new Image().withS3Object(new S3Object().withName(imageNameA).withBucket(bucketName));
        Image imageB = new Image().withS3Object(new S3Object().withName(imageNameB).withBucket(bucketName));

        CompareFacesRequest request = new CompareFacesRequest()
            .withSourceImage(imageA)
            .withTargetImage(imageB)
            .withSimilarityThreshold(0F);

        CompareFacesResult result = client.compareFaces(request);

        PairedKey pKey = new PairedKey(imageNameA, imageNameB);
        PairedKey revKey = new PairedKey(imageNameB, imageNameA);

        //This will only work for one face
        Float similarity = result.getFaceMatches().get(0).getSimilarity();

        // f(a,b) = f(b,a);
        similarityScores.put(pKey, similarity);
        similarityScores.put(revKey, similarity);
        
    }

    public Map<PairedKey, Float> getSimilarityScores(){
        return similarityScores;
    }

    public static RekognizeComparator getInstance(){
        if(instance == null){
            instance = new RekognizeComparator();
        }
        return instance;
    }
    
    public void reset(){
        similarityScores.clear();
    }

}