package com.learnkafka.intg.controller;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3) // kafka zookeper ayaga kaldırmadan,test icin bir kafka generate olur,test bitince silinir.
//yaml class gerekecek kafka icin yapilan conf.'larin embededKafka ile ovverride edilmesi gerek.
@TestPropertySource(properties = 
       {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsControllerIntegrationTest {
	 @Autowired
	 TestRestTemplate restTemplate;

	  @Autowired
	  EmbeddedKafkaBroker embeddedKafkaBroker;
	  
	
	  
	 private Consumer<Integer,String> consumer;
	 
	  @BeforeEach
	  void setUp() {
		  Map<String,Object> configs =new HashMap<>(KafkaTestUtils.consumerProps("group1" ,"true", embeddedKafkaBroker));
		  consumer=new DefaultKafkaConsumerFactory(configs,new IntegerDeserializer(),new StringDeserializer()).createConsumer();
		  embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
	  }
     @AfterEach
	  void tearrDown() {
		  consumer.close();
	  }
	  
	    @Test
	    @Timeout(5)
	    void postLibraryEvent() throws InterruptedException {
	        //given
	        Book book = Book.builder()
	                .bookeId(123)
	                .bookAuthor("Dilip")
	                .bookName("Kafka using Spring Boot")
	                .build();

	        LibraryEvent libraryEvent = LibraryEvent.builder()
	                .libraryEventId(null)
	                .book(book)
	                .build();
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
	        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

	        //when
	        ResponseEntity<LibraryEvent> responseEntity = restTemplate.exchange("/v1/libraryEvent", HttpMethod.POST, request, LibraryEvent.class);

	        //then
	        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
	     String expectedResponse="{\"libraryEventType\":\"NEW\",\"libraryEventId\":null,\"book\":{\"bookeId\":123,\"bookName\":\"Kafka using Spring Boot\",\"bookAuthor\":\"Dilip\"}}";
	       ConsumerRecord<Integer,String> consumerRecord= KafkaTestUtils.getSingleRecord(consumer, "library-events");
	       System.out.println("TEST BEGIN**********************************************************************");
	       System.out.println(consumerRecord.value());
	 	  
	 	   assertEquals(consumerRecord.value(), expectedResponse);
	    }
	    
	    @Test
	    @Timeout(10)
	    void putLibraryEvent() throws InterruptedException {
	        //given
	        Book book = Book.builder()
	                .bookeId(123)
	                .bookAuthor("Dilip")
	                .bookName("Kafka using Spring Boot")
	                .build();

	        LibraryEvent libraryEvent = LibraryEvent.builder()
	                .libraryEventId(110)
	                .book(book)
	                .build();
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
	        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

	        //when
	        ResponseEntity<LibraryEvent> responseEntity = restTemplate.exchange("/v1/libraryEvent", HttpMethod.PUT, request, LibraryEvent.class);
	        System.out.println("PUT TEST BEGIN**********************************************************************");
	        System.out.println(responseEntity.getStatusCode());
	        //then
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	     
	       String expectedResponse="{\"libraryEventType\":\"UPDATE\",\"libraryEventId\":110,\"book\":{\"bookeId\":123,\"bookName\":\"Kafka using Spring Boot\",\"bookAuthor\":\"Dilip\"}}";
	       ConsumerRecord<Integer,String> consumerRecord= KafkaTestUtils.getSingleRecord(consumer, "library-events");
	       assertEquals(consumerRecord.value(), expectedResponse);
	       
	    }
	    
}
