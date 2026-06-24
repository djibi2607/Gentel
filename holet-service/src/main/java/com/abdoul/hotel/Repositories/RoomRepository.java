package com.abdoul.hotel.Repositories;

import com.abdoul.hotel.Entities.RoomModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<RoomModel, Long> {

}
