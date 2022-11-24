package com.talentia.pocsecurity.service.impl;

import com.talentia.pocsecurity.domain.UserEntity;
import com.talentia.pocsecurity.domain.UserPrincipal;
import com.talentia.pocsecurity.dto.UserDto;
import com.talentia.pocsecurity.enumeration.Role;
import com.talentia.pocsecurity.errors.exception.*;
import com.talentia.pocsecurity.mapper.UserEntityMapper;
import com.talentia.pocsecurity.repository.UserEntityRepository;
import com.talentia.pocsecurity.service.LoginAttemptService;
import com.talentia.pocsecurity.service.UserEntityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static com.talentia.pocsecurity.constant.UserImplConstant.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;


@Service
@Transactional
@Qualifier("userDetailsService")
@Slf4j
public class UserEntityServiceImpl implements UserEntityService, UserDetailsService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final UserEntityMapper userEntityMapper;

    @Autowired
    public UserEntityServiceImpl(UserEntityRepository userEntityRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService, UserEntityMapper userEntityMapper) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.userEntityMapper = userEntityMapper;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userEntityRepository.findUserByUsername(username);
        if (user == null) {
            log.error(NO_USER_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLogInDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userEntityRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info(USER_FROM_USER_DETAILS_SERVICE + username);
            return userPrincipal;
        }

    }

    @Override
    public UserDto register(UserDto userDto) {
        validateNewUsernameAndEmail(EMPTY, userDto.getUsername(), userDto.getEmail());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        UserEntity admin = UserEntity.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(encodedPassword)
                .isActive(true)
                .isNotLocked(true)
                .authorities(userDto.getRole().getAuthorities())
                .role(userDto.getRole())
                .build();
        log.info(password);
        return userEntityMapper.entityToDto(userEntityRepository.save(admin));
    }

    @Override
    public UserDto addNewUser(UserDto userDto) {
        validateNewUsernameAndEmail(EMPTY, userDto.getUsername(), userDto.getEmail());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        UserEntity userEntity = UserEntity.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(encodedPassword)
                .isActive(userDto.isActive())
                .isNotLocked(userDto.isNotLocked())
                .authorities(userDto.getRole().getAuthorities())
                .role(userDto.getRole())
                .build();
        log.info(password);
        return userEntityMapper.entityToDto(userEntityRepository.save(userEntity));
    }


    @Override
    public List<UserEntity> getUsers() {
        return userEntityRepository.findAll();
    }

    @Override
    public UserEntity findUserByUsername(String username) {
        return userEntityRepository.findUserByUsername(username);
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userEntityRepository.findUserByEmail(email);
    }

    @Override
    public void deleteUser(String username)  {
        UserEntity userEntity = userEntityRepository.findUserByUsername(username);
        userEntityRepository.deleteById(userEntity.getId());
    }

    @Override
    public void resetPassword(String email){
        UserEntity userEntity = userEntityRepository.findUserByEmail(email);
        if (userEntity == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, NO_USER_BY_EMAIL + email);
        }
        String password = generatePassword();
        userEntity.setPassword(encodePassword(password));
        log.info(password);
        userEntityRepository.save(userEntity);
    }

    private void validateLoginAttempt(UserEntity userEntity) {
        if (userEntity.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(userEntity.getUsername())) {
                userEntity.setNotLocked(false);
            } else {
                userEntity.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(userEntity.getUsername());
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }


    private UserEntity validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) {
        UserEntity userEntityByNewUsername = findUserByUsername(newUsername);
        UserEntity userEntityByNewEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUsername)) {
            UserEntity currentUserEntity = findUserByUsername(currentUsername);
            if (currentUserEntity == null) {
                throw new BusinessException(HttpStatus.NOT_FOUND, USER_NOT_FOUND + currentUsername);
            }
            if (userEntityByNewUsername != null && !currentUserEntity.getId().equals(userEntityByNewUsername.getId())) {
                throw new BusinessException(HttpStatus.CONFLICT, USERNAME_EXISTS);
            }
            if (userEntityByNewEmail != null && !currentUserEntity.getId().equals(userEntityByNewEmail.getId())) {
                throw new BusinessException(HttpStatus.CONFLICT, EMAIL_EXISTS);
            }
            return currentUserEntity;
        } else {
            if (userEntityByNewUsername != null) {
                throw new BusinessException(HttpStatus.CONFLICT, USERNAME_EXISTS);
            }
            if (userEntityByNewEmail != null) {
                throw new BusinessException(HttpStatus.CONFLICT, EMAIL_EXISTS);
            }
            return null;
        }
    }

}