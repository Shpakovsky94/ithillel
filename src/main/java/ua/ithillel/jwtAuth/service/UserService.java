package ua.ithillel.jwtAuth.service;

import ua.ithillel.jwtAuth.dto.UserDto;
import ua.ithillel.jwtAuth.entity.User;

import java.security.Principal;

public interface UserService {
    User saveUser(User user);

    UserDto findByUsername(String username);

    UserDto login(Principal principal);
}
