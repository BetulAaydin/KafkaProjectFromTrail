package com.learnkafka.unit.controller;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.controller.LibraryEventController;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.procuder.LibsraryEventProcuder;

@WebMvcTest(LibraryEventController.class)
@AutoConfigureMockMvc
public class LibraryEventsControllerUnitTest {
	
	@Autowired
	MockMvc mocjMvc;
	
	@MockBean
	LibsraryEventProcuder libsraryEventProcuder; //LibraryEventsControllerUnitTest icinde autowired edilmis. Biz bu kismida moclayarak sadece post islemini test edeceğiz.
	
	private ObjectMapper objectmapper=new ObjectMapper();
	@Test
	void postLibraryEvent() throws Exception {
		Book book = Book.builder()
                .bookeId(123)
                .bookAuthor("Dilip")
                .bookName("Kafka using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();
        
        String json=objectmapper.writeValueAsString(libraryEvent);
        doNothing().when(libsraryEventProcuder).sendLibraryEventVoid(isA(LibraryEvent.class));
        
        mocjMvc.perform(post("/v1/libraryEvent").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
        
	}
	
	@Test
	void postLibraryEventCustomeMessage() throws Exception {
		Book book = Book.builder()
                .bookeId(null)
                .bookAuthor("Dilip")
                .bookName("Kafka using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();
        
        String json=objectmapper.writeValueAsString(libraryEvent);
        doNothing().when(libsraryEventProcuder).sendLibraryEventVoid(isA(LibraryEvent.class));
       
        mocjMvc.perform(post("/v1/libraryEvent").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
        
	}

}
