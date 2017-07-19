package com.serverless

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ComponentScan

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.serverless.lambda.Request
import com.serverless.lambda.Response
import com.serverless.service.DispatcherService

import groovy.transform.Memoized
import groovy.util.logging.Log4j

@SpringBootApplication
@EnableAutoConfiguration(exclude=[DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class])
@ComponentScan(basePackages = "com.serverless")
@Log4j
class LambdaHandler implements RequestHandler<Map, Response> {

  static void main(String[] args) throws Exception {
    LambdaHandler.newInstance().getApplicationContext(args)
  }

  @Memoized
  ApplicationContext getApplicationContext(String[] args = []) {
    return SpringApplication.run(LambdaHandler.class, args)
  }

  @Override
  Response handleRequest(Map input, Context context) {
    final Request request = new Request(input, context)
    DispatcherService dispatcher = getApplicationContext().getBean(DispatcherService.class)
    return dispatcher.dispatch(request)
  }
}
