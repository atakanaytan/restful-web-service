package com.mobile.app.ws.mobileappws.io.repository;

import com.mobile.app.ws.mobileappws.io.entity.AddressEntity;
import com.mobile.app.ws.mobileappws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {

    List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
}
