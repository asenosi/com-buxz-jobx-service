package com.buxz.dev.combuxzjobxservice.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class UserAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, updatable = false, nullable = false)
    private String email;
    private String firstName;
    private String lastName;
    private String cellNumber;
    @Column(unique = true)
    private String userName;
    private LocalDateTime dateCreated;
    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id")
    private List<UserProfileEntity> userProfiles;
    //    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    //    @JoinColumn(name = "user_profile")
    //    private UserAccountProfile userAccountProfile;
    //
}
