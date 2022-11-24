package com.talentia.pocsecurity.dto;

import com.talentia.pocsecurity.enumeration.Role;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;


@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {

	private Long id;

	private String firstName;
	private String lastName;
	@NotBlank(message = "username is mandatory")
	private String username;
	private String password;
	@Email(message = "Email is not valid",regexp = "^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")
	@NotBlank(message = "Email cannot be empty")
	private String email;
	private Date lastLoginDate;
	private Date logInDateDisplay;
	private Role role;
	private String[] authorities;
	private boolean isActive;
	private boolean isNotLocked;
	


}

