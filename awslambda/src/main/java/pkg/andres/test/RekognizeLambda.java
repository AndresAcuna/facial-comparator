package pkg.andres.test;

import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.json.JSONArray;
import org.json.JSONObject;

public class RekognizeLambda implements RequestHandler<Object, Object> {

    private static final String BUCKET_NAME = "andres-lambda-rekog-bucket";


    @Override
    public Object handleRequest(Object input, Context context) {
      

        JSONObject jsonResponse = new JSONObject();

        // BasicAWSCredentials creds = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);

        AwsS3Getter s3Helper = AwsS3Getter.getInstance();
        List<String> filenames = s3Helper.getFileNames(BUCKET_NAME);

        JSONArray jsonImages = new JSONArray();

        for(String name: filenames){
            JSONObject imageData = new JSONObject();
            imageData.put("key", name);
            imageData.put("url", "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + name);
            jsonImages.put(imageData);
        }  
        
        RekognizeComparator rekognizeHelper = RekognizeComparator.getInstance();

        for(int i = 0; i < filenames.size(); i++){
            for(int j = i+1; j < filenames.size(); j++) {
                rekognizeHelper.compare(filenames.get(i), filenames.get(j), BUCKET_NAME);
            }
        }

        Map<PairedKey, Float> scores = rekognizeHelper.getSimilarityScores();

        JSONArray jsonScores = new JSONArray();

        for(PairedKey pKey: scores.keySet()){
            JSONObject element = new JSONObject();
            element.put("key",pKey.getJSON());
            element.put("score", scores.get(pKey));
            jsonScores.put(element);
        }

        jsonResponse.put("results", jsonScores);
        jsonResponse.put("images", jsonImages);
        jsonResponse.put("fileCount", filenames.size());
        jsonResponse.put("resultCount", scores.size());
        jsonResponse.put("bucket", BUCKET_NAME);
        jsonResponse.put("region", Regions.US_EAST_1);

        rekognizeHelper.reset();

        return jsonResponse.toString();
    }

    
}
