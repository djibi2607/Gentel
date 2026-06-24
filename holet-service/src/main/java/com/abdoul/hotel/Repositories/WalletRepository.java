package com.abdoul.hotel.Repositories;

import com.abdoul.hotel.Entities.WalletModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<WalletModel, Long> {

}
