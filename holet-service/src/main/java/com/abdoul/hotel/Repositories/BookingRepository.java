package com.abdoul.hotel.Repositories;

import com.abdoul.hotel.Entities.BookingModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingModel, Long> {

}
