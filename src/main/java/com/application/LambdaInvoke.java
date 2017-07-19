package com.application;
import java.nio.ByteBuffer;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

/**
 * Created by itahg on 03/05/2017. 
 */
public class LambdaInvoke {

    public static void main(String[] args)  {

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

    }
}