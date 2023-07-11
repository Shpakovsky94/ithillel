package ua.ithillel.jwtAuth.dto;

import lombok.Data;
import ua.ithillel.jwtAuth.entity.Role;

@Data
public class UserDto {

    private Long id;

    private String username;

    private String name;

    private String email;

    private boolean isPremium;

    private Role role;

    private String token;
}
