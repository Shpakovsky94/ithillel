package ua.ithillel.jwtAuth.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "USER")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME", length = 100, nullable = false, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 100)
    private String password;

    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "EMAIL", length = 100, unique = true)
    private String email;

    @Column(name = "IS_PREMIUM")
    private boolean isPremium;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Transient
    private String token;
}
