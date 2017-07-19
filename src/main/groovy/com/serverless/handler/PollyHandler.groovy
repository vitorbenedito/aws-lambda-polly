package com.serverless.handler

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.amazonaws.services.polly.model.OutputFormat
import com.amazonaws.util.IOUtils
import com.serverless.lambda.Request
import com.serverless.lambda.Response
import com.serverless.service.PollyRequest

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

@Component
@Log4j
@CompileStatic
class PollyHandler implements Handler {

  @Autowired
  private PollyRequest pollyRequest

  @Override
  boolean route(Request request) {
    request.resourcePath() == '/polly/speech' && request.httpMethod() == 'POST'
  }

  @Override
  Response respond(final Request request) {
	  
	  	//def Map json = (Map) new JsonSlurper().parseText( request.body() )
		def String text =  request.body()
		//def String format = json.format
   		def InputStream is = pollyRequest.synthesize(text, OutputFormat.Mp3)
		def byte[] response = IOUtils.toByteArray(is)
		
		Response.builder()
		   .statusCode(200)
		   .body(Base64.getEncoder().encode(response))
		   .build()
  }
}
