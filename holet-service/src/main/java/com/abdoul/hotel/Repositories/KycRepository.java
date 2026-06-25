package com.abdoul.hotel.Repositories;

import com.abdoul.hotel.Entities.KycModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycRepository extends JpaRepository<KycModel, Long> {
}
