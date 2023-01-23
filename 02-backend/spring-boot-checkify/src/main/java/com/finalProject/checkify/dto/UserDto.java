package com.finalProject.checkify.dto;

import com.finalProject.checkify.entity.Role;
import lombok.Data;

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
