package com.abdoul.hotel.Repositories;

import com.abdoul.hotel.Config.KycType;
import com.abdoul.hotel.Entities.KycModel;
import com.abdoul.hotel.Entities.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycRepository extends JpaRepository<KycModel, Long> {

    KycModel findByUserAndKycType (UserModel user, KycType kycType);
}
