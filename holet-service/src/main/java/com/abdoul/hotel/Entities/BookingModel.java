package com.abdoul.hotel.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class BookingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal total;

    private String note;

    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "booking_user")
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "booking_room")
    private RoomModel roomModel;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime bookedAt;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @PrePersist
    public void onCreate (){
        this.bookedAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }
}
