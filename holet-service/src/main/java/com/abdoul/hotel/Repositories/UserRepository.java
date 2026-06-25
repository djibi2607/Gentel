package com.abdoul.hotel.Repositories;

import com.abdoul.hotel.Entities.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    UserModel findByEmailOrPhone (String email, String phone);

}
