package com.talentia.pocsecurity.errors;

import lombok.*;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorDetails {

	private String objectName;
	private String field;
	private String message;


}
