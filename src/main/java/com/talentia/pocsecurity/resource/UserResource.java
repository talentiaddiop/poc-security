package com.talentia.pocsecurity.resource;

import com.talentia.pocsecurity.domain.UserEntity;
import com.talentia.pocsecurity.domain.UserPrincipal;
import com.talentia.pocsecurity.dto.UserDto;
import com.talentia.pocsecurity.errors.HttpResponse;
import com.talentia.pocsecurity.mapper.UserEntityMapper;
import com.talentia.pocsecurity.remote.TacRemoteService;
import com.talentia.pocsecurity.service.AuthenticationService;
import com.talentia.pocsecurity.service.UserEntityService;
import com.talentia.pocsecurity.utility.JWTTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static com.talentia.pocsecurity.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping("/api/user")
@Slf4j


public class UserResource {
    private final AuthenticationService authenticationService;
    private final UserEntityService userEntityService;
    private final JWTTokenProvider jWTTokenProvider;
    private final UserEntityMapper mapper;
    private final TacRemoteService tacRemoteService;
  

    @Autowired
    public UserResource(AuthenticationService authenticationService, UserEntityService userEntityService, JWTTokenProvider jWTTokenProvider, UserEntityMapper mapper, TacRemoteService tacRemoteService) {
        this.authenticationService = authenticationService;
        this.userEntityService = userEntityService;
        this.jWTTokenProvider = jWTTokenProvider;
        this.mapper = mapper;
        this.tacRemoteService = tacRemoteService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid UserDto userDto) {
        authenticationService.authenticate(userDto.getUsername(), userDto.getPassword());
        UserEntity loginUserEntity = userEntityService.findUserByUsername(userDto.getUsername());
        UserPrincipal principal = new UserPrincipal(loginUserEntity);
        HttpHeaders jwtHeader = getJwtHeader(principal);
        return ResponseEntity.ok().headers(jwtHeader).body(mapper.entityToDto(loginUserEntity));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Validated @RequestBody UserDto userDto){
        UserDto newUserEntity = userEntityService.register(userDto);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/find/" + newUserEntity.getUsername()).toUriString());
        return ResponseEntity.created(uri).body(newUserEntity);
    }

    @PostMapping("/add")
    public ResponseEntity<UserDto> create(@Validated @RequestBody UserDto userDto){
        UserDto newUserEntity = userEntityService.addNewUser(userDto);
        return ResponseEntity.ok().body(newUserEntity);
    }



    @GetMapping("/find/{username}")
    public ResponseEntity<UserEntity> getUser(@PathVariable("username")String username) {
        UserEntity userEntity = userEntityService.findUserByUsername(username);

        return ResponseEntity.ok().body(userEntity);
    }

    @GetMapping("/list")
    @Operation(summary = "get users", description = "get users")
    @PreAuthorize("hasAuthority('user:read')")

    public ResponseEntity<List<UserEntity>> getAllUsers() {
       // UserDto userTac = callTacAccounting("admin");
        List<UserEntity> userEntities = userEntityService.getUsers();
        return ResponseEntity.ok().body(userEntities);
    }

	@GetMapping("/resetpassword/{email}")
	public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)  {
		userEntityService.resetPassword(email);
		return response(OK, "Password changed successfully");
	}

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) {
        userEntityService.deleteUser(username);
        return response(OK, "User deleted successfully");
    }

    private HttpHeaders getJwtHeader(UserPrincipal user){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWT_TOKEN_HEADER, jWTTokenProvider.generateJwtToken(user));
        return httpHeaders;
    }


    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),
                httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
    }

    private UserDto callTacAccounting(String username) {
        return tacRemoteService.getUser(" Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJVc2VyIERlbWF0IFBvcnRhbCIsInN1YiI6IjEiLCJyb2xlIjoiUk9MRV9TVVBFUl9BRE1JTiIsImlzcyI6IkdldCBBcnJheXMsIExMQy4iLCJleHAiOjE2NjkyMjI1NjUsImlhdCI6MTY2OTIyMDc2NSwiYXV0aG9yaXRpZXMiOlsidXNlcjpyZWFkIiwidXNlcjpjcmVhdGUiLCJ1c2VyOnVwZGF0ZSIsInVzZXI6ZGVsZXRlIl0sInVzZXJuYW1lIjoiYWRtaW4ifQ.gEct9G6ewTulfhGMw4Fz2bY561hOYYtKHQgIvXzIMV7w0_krOGDZmjc_gDcqt9JIcZN26vlMXn4r3I8kBcAP3w",username);
    }

}
