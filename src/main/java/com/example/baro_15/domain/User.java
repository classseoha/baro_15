package com.example.baro_15.domain;

import jakarta.persistence.*;
import lombok.*;

import javax.accessibility.AccessibleRelation;

@Entity
@Table(name = "users")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;

    @Builder
    public User(String email, String password, UserRole userRole) {

        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }
}
