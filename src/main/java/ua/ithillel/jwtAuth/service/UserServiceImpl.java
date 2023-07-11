package ua.ithillel.jwtAuth.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.ithillel.jwtAuth.dto.UserDto;
import ua.ithillel.jwtAuth.entity.User;
import ua.ithillel.jwtAuth.repository.UserRepository;
import ua.ithillel.jwtAuth.security.JwtTokenProvider;

import java.security.Principal;

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
    public UserDto login(Principal principal) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        UserDto user = findByUsername(authenticationToken.getName());

        if (user != null) {
            user.setToken(jwtTokenProvider.generateToken(authenticationToken));
        }
        return user;
    }
}
