package com.learnkafka.domain;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter

public class LibraryEvent {
	  private LibraryEventType libraryEventType;
	  private Integer libraryEventId;
	 
	  @Valid
	  private Book book;
}
