package com.talentia.pocsecurity.remote;

import com.talentia.pocsecurity.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="tac-accounting", url="${tac.url.api.accounting}")
public interface TacRemoteService {
    @GetMapping("/api/user/find/{username}")
    UserDto getUser(@RequestHeader("Authorization") String token, @PathVariable String username);
}
