package com.abdoul.hotel.Entities;

import com.abdoul.hotel.Config.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String password;

    private boolean faEnabled;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @Enumerated(value = EnumType.STRING)
    private UserType userType = UserType.User;

    @Column(name = "is_deleted")
    private boolean deleted;

    @PrePersist
    public void onCreate (){
        this.faEnabled = false;
        this.deleted = false;
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    @PreUpdate
    public void onUpdate (){
        this.updatedAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    @OneToOne(mappedBy = "user")
    private WalletModel userWallet;

    @OneToMany(mappedBy = "user")
    private List<KycModel> kycs;
}
