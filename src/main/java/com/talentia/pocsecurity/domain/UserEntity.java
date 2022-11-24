package com.talentia.pocsecurity.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.talentia.pocsecurity.enumeration.Role;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.Date;

@Entity
@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Long id;
	private String firstName;
	private String lastName;
	private String username;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	@Email
	private String email;
	private Date lastLoginDate;
	private Date logInDateDisplay;
	@Enumerated(EnumType.STRING)
	private Role role;
	private String[] authorities;
	@Column(name = "isActive")
	private boolean isActive;
	@Column(name = "isNotLocked")
	private boolean isNotLocked;
	


}

