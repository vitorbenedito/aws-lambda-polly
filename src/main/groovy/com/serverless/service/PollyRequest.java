package com.serverless.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.Voice;

@Component
public class PollyRequest {
	
	private static String defaultMessage = "Message test";

	public InputStream synthesize(String text, OutputFormat format) throws IOException {
		
		AmazonPolly polly = AmazonPollyClientBuilder.defaultClient();
				
		DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

		DescribeVoicesResult describeVoicesResult = polly.describeVoices(describeVoicesRequest);
		Voice voice = describeVoicesResult.getVoices().get(0);
		
		SynthesizeSpeechRequest synthReq = new SynthesizeSpeechRequest().withText(text != null ? text : defaultMessage).withVoiceId(voice.getId())
						.withOutputFormat(format);
		SynthesizeSpeechResult synthRes = polly.synthesizeSpeech(synthReq);

		return synthRes.getAudioStream();
	}

	
} 