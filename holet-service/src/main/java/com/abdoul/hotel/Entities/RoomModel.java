package com.abdoul.hotel.Entities;

import com.abdoul.hotel.Config.RoomType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class RoomModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomNumber;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Column (nullable = false, precision = 7, scale = 2)
    private BigDecimal nightPrice;

    private boolean available = true;

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

    @ManyToOne
    @JoinColumn(name = "room_hotel")
    private HotelModel hotel;
}
