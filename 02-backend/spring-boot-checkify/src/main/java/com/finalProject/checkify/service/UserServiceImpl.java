package com.finalProject.checkify.service;

import com.finalProject.checkify.dao.UserRepository;
import com.finalProject.checkify.dto.UserDto;
import com.finalProject.checkify.entity.User;
import com.finalProject.checkify.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public UserDto findByUsername(String username) {
        UserDto userDto = null;

        User userDb = userRepository.findByUsername(username).orElse(null);
        if (userDb != null) {
            userDto = modelMapper.map(userDb, UserDto.class);
        }
        return userDto;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDto login(Principal principal) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        UserDto user = findByUsername(authenticationToken.getName());

        if (user != null) {
            user.setToken(jwtTokenProvider.generateToken(authenticationToken));
        }
        return user;
    }
}
