package com.learnkafka.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Book {
	@NotNull
	private Integer bookeId;
	@NotBlank
	private String bookName;
	@NotBlank(message = "bookAuthor is mandatory")
	private String bookAuthor;

}
