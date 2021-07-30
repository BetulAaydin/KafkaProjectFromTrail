package com.learnkafka.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.domain.LibraryEventType;
import com.learnkafka.procuder.LibsraryEventProcuder;

@RestController
public class LibraryEventController {
	@Autowired
	LibsraryEventProcuder procuder;
	
	@PostMapping("/v1/libraryEvent")
	public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Validated LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException{
		libraryEvent.setLibraryEventType(LibraryEventType.NEW);
		procuder.sendLibraryEvent(libraryEvent);
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}

	@PutMapping("/v1/libraryEvent")
	public ResponseEntity<?> putLibraryEvent(@RequestBody @Validated LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException{
		 if(libraryEvent.getLibraryEventId()==null){
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the LibraryEventId");
	        }

		libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
		//procuder.sendLibraryEvent(libraryEvent);
		return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
	}
}
