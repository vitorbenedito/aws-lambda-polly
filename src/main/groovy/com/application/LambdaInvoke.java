package com.application;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Base64;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 * Created by itahg on 03/05/2017. 
 */
public class LambdaInvoke {

    public static void main(String[] args) throws JavaLayerException {

        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName("aws-lambda-polly-dev-users")
                .withPayload("{\"resource\":\"/polly/speech\",\"path\":\"/polly/speech\",\"httpMethod\":\"POST\",\"headers\":null,\"queryStringParameters\":null,\"pathParameters\":null,\"stageVariables\":null,\"requestContext\":{\"path\":\"/polly/speech\",\"accountId\":\"028625723143\",\"resourceId\":\"vy86ah\",\"stage\":\"test-invoke-stage\",\"requestId\":\"test-invoke-request\",\"identity\":{\"cognitoIdentityPoolId\":null,\"accountId\":\"028625723143\",\"cognitoIdentityId\":null,\"caller\":\"028625723143\",\"apiKey\":\"test-invoke-api-key\",\"sourceIp\":\"test-invoke-source-ip\",\"accessKey\":\"ASIAIS5LNGHUGEKRHBSA\",\"cognitoAuthenticationType\":null,\"cognitoAuthenticationProvider\":null,\"userArn\":\"arn:aws:iam::028625723143:root\",\"userAgent\":\"Apache-HttpClient/4.5.x (Java/1.8.0_112)\",\"user\":\"028625723143\"},\"resourcePath\":\"/polly/speech\",\"httpMethod\":\"POST\",\"apiId\":\"hwo0mbid4f\"},\"body\":\"test 123\",\"isBase64Encoded\":false}");

        AWSCredentials awsCreds = new DefaultAWSCredentialsProviderChain().getCredentials();

        AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

        InvokeResult invokeResult = null;

        try {
            invokeResult = awsLambda.invoke(invokeRequest);
        }
        catch (Exception e) {

        }

        System.out.println(invokeResult.getStatusCode());

        ByteBuffer byteBuffer = invokeResult.getPayload();
        String result = "";
        try {
        	result = new String(byteBuffer.array(), "UTF-8");
        }catch (Exception e) {

        }

        System.out.println(result);
        
        try {
        	ObjectMapper mapper = new ObjectMapper();
        	JsonFactory factory = mapper.getFactory();
        	JsonParser jsonParser = factory.createParser(result);
        	JsonNode node = mapper.readTree(jsonParser);
        	
        	byte[] decoded = Base64.getDecoder().decode(node.get("body").asText());
        	ByteBuffer buf = ByteBuffer.wrap(decoded);
        	
			File file = new File("out.mp3");

			// append or overwrite the file
			boolean append = false;

			FileChannel channel = new FileOutputStream(file, append).getChannel();

			// Writes a sequence of bytes to this channel from the given buffer.
			channel.write(buf);

			// close the channel
			channel.close();
			
			//create an MP3 player
	        ByteArrayInputStream bis = new ByteArrayInputStream(decoded);
	      		AdvancedPlayer player = new AdvancedPlayer(bis,
	      				javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());

	      		player.setPlayBackListener(new PlaybackListener() {
	      			@Override
	      			public void playbackStarted(PlaybackEvent evt) {
	      				System.out.println("Playback started");
	      				System.out.println("test");
	      			}
	      			
	      			@Override
	      			public void playbackFinished(PlaybackEvent evt) {
	      				System.out.println("Playback finished");
	      			}
	      		});
	      		
	      		
	      		// play it!
	      		player.play();

		}
		catch (IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
        
      

        


    }
}