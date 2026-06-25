package com.abdoul.hotel.Entities;

import com.abdoul.hotel.Config.KycStatus;
import com.abdoul.hotel.Config.KycType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "kycs")
@Getter
@Setter
@NoArgsConstructor
public class KycModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private KycType kycType;

    @Enumerated(value = EnumType.STRING)
    private KycStatus kycStatus = KycStatus.Pending;

    private String url;

    private boolean remainder = false;

    @ManyToOne
    @JoinColumn(name = "kyc_user")
    private UserModel user;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @PrePersist
    public void onCreate (){
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }
}
