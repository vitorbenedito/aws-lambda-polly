package com.serverless;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.HttpStatus;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.serverless.proxy.internal.model.ApiGatewayRequestContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.LanguageCode;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.Voice;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LambdaFunctionHandler implements RequestHandler<ApiGatewayRequest,ApiGatewayProxyResponse>  {
	

	private static final String DEST_BUCKET_NAME = "test-bucket-vitor";
    private static final String AUDIO_FILE_PREFIX = "mp3/";
	
    public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest apiRequest, Context context) {

        try {
            System.out.println("Request: " + apiRequest);
        	ObjectMapper mapper = new ObjectMapper();
        	PollyText pollyText = mapper.readValue(apiRequest.getBody(), PollyText.class);
        	System.out.println("Polly Text: " + pollyText);
            AmazonPolly polly = AmazonPollyClientBuilder.defaultClient();
			
    		DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();
    		
    		String laguageCode = pollyText.getLanguageCode();
    		try{
    			describeVoicesRequest.setLanguageCode( LanguageCode.fromValue(laguageCode) );
    		}catch(IllegalArgumentException ex){
    			describeVoicesRequest.setLanguageCode(LanguageCode.EnUS);
    		}

    		DescribeVoicesResult describeVoicesResult = polly.describeVoices(describeVoicesRequest);
    		Voice voice = describeVoicesResult.getVoices().get(0);
    		
    		SynthesizeSpeechRequest synthReq = new SynthesizeSpeechRequest().withText(pollyText.getText()).withVoiceId(voice.getId())
    						.withOutputFormat(OutputFormat.Mp3);
    		SynthesizeSpeechResult synthRes = polly.synthesizeSpeech(synthReq);
    		
            AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
            
            try {
            	
            	byte[] bytes = IOUtils.toByteArray(synthRes.getAudioStream());
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(bytes.length);
                 
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                PutObjectRequest request = new PutObjectRequest(DEST_BUCKET_NAME, AUDIO_FILE_PREFIX + pollyText.getLabel() + ".mp3", byteArrayInputStream, metadata);
                
                System.out.println("Uploading a new object to S3 from a file\n");
                s3client.putObject(request);
   
                return new ApiGatewayProxyResponse(HttpStatus.SC_OK, null, null);
                
             } catch (AmazonServiceException ase) {
                System.out.println("Caught an AmazonServiceException, which " +
                		"means your request made it " +
                        "to Amazon S3, but was rejected with an error response" +
                        " for some reason.");
                System.out.println("Error Message:    " + ase.getMessage());
                System.out.println("HTTP Status Code: " + ase.getStatusCode());
                System.out.println("AWS Error Code:   " + ase.getErrorCode());
                System.out.println("Error Type:       " + ase.getErrorType());
                System.out.println("Request ID:       " + ase.getRequestId());
            } catch (AmazonClientException ace) {
                System.out.println("Caught an AmazonClientException, which " +
                		"means the client encountered " +
                        "an internal error while trying to " +
                        "communicate with S3, " +
                        "such as not being able to access the network.");
                System.out.println("Error Message: " + ace.getMessage());
            } catch (IOException e) {
				
				e.printStackTrace();
			}
            
        } catch (AmazonServiceException e) {
            System.out.println("Error Message: " + e.getMessage());
        } catch (JsonParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   
        
        return new ApiGatewayProxyResponse(HttpStatus.SC_BAD_REQUEST, null, null);
    }


}