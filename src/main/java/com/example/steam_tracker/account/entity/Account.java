package com.example.steam_tracker.account.entity;

import com.example.steam_tracker.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "account")
@NoArgsConstructor
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false,unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role = RoleType.USER;

    public Account(String username, String password, String email, String nickname)
    {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

}



