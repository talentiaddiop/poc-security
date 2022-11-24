package com.talentia.pocsecurity.service;

import com.talentia.pocsecurity.domain.UserEntity;
import com.talentia.pocsecurity.dto.UserDto;

import java.util.List;

public interface UserEntityService {

	UserDto register(UserDto userDto) ;

	UserDto addNewUser(UserDto userDto) ;


	List<UserEntity> getUsers();

	UserEntity findUserByUsername(String username);
	
	UserEntity findUserByEmail(String email);
	
	void deleteUser(String username);

	void resetPassword(String email) ;

}
