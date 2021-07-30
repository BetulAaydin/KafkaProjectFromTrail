package com.learnkafka.intg.producer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.procuder.LibsraryEventProcuder;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerUnitTest {
	@Mock
	KafkaTemplate<Integer, String> kafkatemplate;

	@Spy // Mock bu class methodları gercekten execute olmaz.Spy ile ObjectMapper 			// wraplenerek ilgili methodları kullanılıyor.
	ObjectMapper objectMapper = new ObjectMapper();

	@InjectMocks
	LibsraryEventProcuder libsraryEventProcuder;

	@Test
	void sendLibraryEvent_Approach2_failure() throws JsonProcessingException, ExecutionException, InterruptedException {
		// given
		Book book = Book.builder().bookeId(123).bookAuthor("Dilip").bookName("Kafka using Spring Boot").build();
		LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();
		SettableListenableFuture future=new SettableListenableFuture();
		future.setException(new RuntimeException("Kafka Hata"));
		
		//libsraryEventProcuder icinde kafkasend methodu simule edilir.
		when(kafkatemplate.send(isA(ProducerRecord.class))).thenReturn(future);
		
		assertThrows(Exception.class,()->libsraryEventProcuder.sendLibraryEvent(libraryEvent).get());	
		
	}
	
	@Test
    void sendLibraryEvent_Approach2_success() throws JsonProcessingException, ExecutionException, InterruptedException {
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
        String record = objectMapper.writeValueAsString(libraryEvent);
        SettableListenableFuture future = new SettableListenableFuture();

        ProducerRecord<Integer, String> producerRecord = new ProducerRecord("library-events", libraryEvent.getLibraryEventId(),record );
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("library-events", 1),
                1,1,342,System.currentTimeMillis(), 1, 2);
        SendResult<Integer, String> sendResult = new SendResult<Integer, String>(producerRecord,recordMetadata);

        future.set(sendResult);
        when(kafkatemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        //when

        ListenableFuture<SendResult<Integer,String>> listenableFuture =  libsraryEventProcuder.sendLibraryEvent(libraryEvent);

        //then
        SendResult<Integer,String> sendResult1 = listenableFuture.get();
        assert sendResult1.getRecordMetadata().partition()==1;

    }

}
