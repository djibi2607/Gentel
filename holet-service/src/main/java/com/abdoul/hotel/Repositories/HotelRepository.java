package com.abdoul.hotel.Repositories;

import com.abdoul.hotel.Entities.HotelModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<HotelModel, Long> {

}
