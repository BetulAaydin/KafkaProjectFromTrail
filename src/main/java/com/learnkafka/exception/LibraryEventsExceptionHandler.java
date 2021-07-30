package com.learnkafka.exception;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class LibraryEventsExceptionHandler {
	
@ExceptionHandler(MethodArgumentNotValidException.class)	
public ResponseEntity<?> handleRequestBody(MethodArgumentNotValidException ex){		
	List<FieldError> fieldError=ex.getBindingResult().getFieldErrors();	
	String responseMessage=fieldError.stream().map(filedMap->filedMap.getField()+"- "+filedMap.getDefaultMessage()).sorted().collect(Collectors.joining(","));
	 System.out.println("errorMessage handler**********************************************************************"+responseMessage);
	log.error("errorMessage {} "+responseMessage);	
	return new ResponseEntity<>(responseMessage,HttpStatus.BAD_REQUEST);
}
}
