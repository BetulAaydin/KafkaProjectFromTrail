package com.learnkafka.procuder;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;

import lombok.extern.slf4j.Slf4j;



@Component
@Slf4j
public class LibsraryEventProcuder {
	
	@Autowired
	KafkaTemplate<Integer,String> kafkaTemplate;
	
    String topic = "library-events";
    
	@Autowired
	ObjectMapper objectMapper;
	//Asenkron producer islemlerinde paralel bir thread icinde ilgili event işlenir ve sonrasındaki kod bagimsiz bir sekilde devam eder
	 public  ListenableFuture<SendResult<Integer,String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException  {
		
	        final Integer key = libraryEvent.getLibraryEventId();
	        final String value = objectMapper.writeValueAsString(libraryEvent);
	        ProducerRecord<Integer,String> producerRecord = buildProducerRecord(key, value, topic);
	        ListenableFuture<SendResult<Integer,String>> listenableFuture =  kafkaTemplate.send(producerRecord);
	        // Kodda topic iletmek için; kafkaTemplate.send("library-events",key,value)
	        //sentDefault method ile topci ismini yaml conf. edilmiş yerden alır.
	        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
	            @Override
	            public void onFailure(Throwable ex) {
	                handleFailure(key, value, ex);
	            }

	            @Override
	            public void onSuccess(SendResult<Integer, String> result) {
	                handleSuccess(key, value, result);
	            }
	        });
	        return listenableFuture;
	    }
	 
	
		 public  void sendLibraryEventVoid(LibraryEvent libraryEvent) throws JsonProcessingException {
			
		        final Integer key = libraryEvent.getLibraryEventId();
		        final String value = objectMapper.writeValueAsString(libraryEvent);

		        ListenableFuture<SendResult<Integer,String>> listenableFuture =  kafkaTemplate.sendDefault(key,value);
		        // Kodda topic iletmek için; kafkaTemplate.send("library-events",key,value)
		        //sentDefault method ile topci ismini yaml conf. edilmiş yerden alır.
		        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
		            @Override
		            public void onFailure(Throwable ex) {
		                handleFailure(key, value, ex);
		            }

		            @Override
		            public void onSuccess(SendResult<Integer, String> result) {
		                handleSuccess(key, value, result);
		            }
		        });
		    
		    }
	 
	 //Senkron producer islemlerinde ilgili servisten success yanit alana kadar beklenir ve sonrasında bir sonraki kod satırına gecilir.
	 public SendResult<Integer,String> sendLibraryEventSynchronize(LibraryEvent libraryEvent) throws JsonProcessingException {			
	        final Integer key = libraryEvent.getLibraryEventId();
	        final String value = objectMapper.writeValueAsString(libraryEvent);
	        SendResult<Integer,String> syncresult=null;
	        try {
			   syncresult =  kafkaTemplate.sendDefault(key,value).get();
			} catch (InterruptedException | ExecutionException e) {
				log.error("InterruptedException/ExecutionException Sending the Message and the exception is {}", e.getMessage());
			}	
	        return syncresult;
	    }
	  private void handleFailure(Integer key, String value, Throwable ex) {
		  
	        log.error("Error Sending the Message and the exception is {}", ex.getMessage());
	        try {
	            throw ex;
	        } catch (Throwable throwable) {
	           // log.error("Error in OnFailure: {}", throwable.getMessage());
	        }


	    }

	    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
	       log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}", key, value, result.getRecordMetadata().partition());
	    }
	    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
	        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));
	        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
	    }
}
