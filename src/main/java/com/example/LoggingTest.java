package com.example;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class LoggingTest {

	Logger logger = (Logger) LoggerFactory.getLogger(LoggingTest.class);
	
	public void testLogging(){
		int counter = 0;
		
		while(counter <= 20){
			
			logger.info("This is a logger message {} ", counter );
			System.out.println("This is a system message " + counter);
			counter++;
		}
	}
}
