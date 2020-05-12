package com.mobile.app.ws.mobileappws.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.mobile.app.ws.mobileappws.io.entity.UserEntity;
import com.mobile.app.ws.mobileappws.io.repository.UserRepository;
import com.mobile.app.ws.mobileappws.service.UserService;
import com.mobile.app.ws.mobileappws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto user) {

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        userEntity.setUserId("testUserId");
        userEntity.setEncryptedPassword("Test");

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, returnValue);

        return returnValue;
    }

}
