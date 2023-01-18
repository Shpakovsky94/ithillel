package com.finalProject.checkify.service;


import com.finalProject.checkify.entity.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    User findByUsername(String username);

    List<User> findAllUsers();
}
