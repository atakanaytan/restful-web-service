package com.mobile.app.ws.mobileappws.io.repository;

import com.mobile.app.ws.mobileappws.io.entity.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

    UserEntity findUserByEmail(String email);
    UserEntity findByUserId(String userId);

}
