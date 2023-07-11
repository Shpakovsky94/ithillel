package ua.ithillel.jwtAuth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ua.ithillel.jwtAuth.dto.UserDto;
import ua.ithillel.jwtAuth.entity.Role;
import ua.ithillel.jwtAuth.entity.User;
import ua.ithillel.jwtAuth.service.UserService;

import java.security.Principal;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/api/user/registration")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        user.setRole(Role.USER);
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
    }

    @GetMapping("/api/user/login")
    public ResponseEntity<?> login(Principal principal) {
//        if (principal == null) {
//            //This should be ok http status because this will be used for logout path.
//            return ResponseEntity.ok(principal);
//        }
        UserDto result = userService.login(principal);
        log.info("logged in userName: {}", result.getName());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/api/user/logout")
    public ResponseEntity<?> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDto user = userService.findByUsername(authentication.getName());

        if (user != null) {
            SecurityContextHolder.getContext().setAuthentication(null);
            log.info("logged out userName: {}", authentication.getName());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
