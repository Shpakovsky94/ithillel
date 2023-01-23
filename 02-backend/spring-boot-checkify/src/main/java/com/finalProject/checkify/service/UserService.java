package com.finalProject.checkify.service;


import com.finalProject.checkify.dto.UserDto;
import com.finalProject.checkify.entity.User;

import java.security.Principal;
import java.util.List;

public interface UserService {
    User saveUser(User user);

    UserDto findByUsername(String username);

    List<User> findAllUsers();

    UserDto login(Principal principal);
}
