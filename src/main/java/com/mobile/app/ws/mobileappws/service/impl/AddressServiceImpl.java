package com.mobile.app.ws.mobileappws.service.impl;

import com.mobile.app.ws.mobileappws.exceptions.UserServiceException;
import com.mobile.app.ws.mobileappws.io.entity.AddressEntity;
import com.mobile.app.ws.mobileappws.io.entity.UserEntity;
import com.mobile.app.ws.mobileappws.io.repository.AddressRepository;
import com.mobile.app.ws.mobileappws.io.repository.UserRepository;
import com.mobile.app.ws.mobileappws.service.AddressService;
import com.mobile.app.ws.mobileappws.shared.dto.AddressDto;
import com.mobile.app.ws.mobileappws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<AddressDto> getAddresses(String userId) {

        List<AddressDto> returnValue = new ArrayList<>();

        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) return returnValue;

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        for (AddressEntity addressEntity : addresses) {
            returnValue.add(modelMapper.map(addressEntity, AddressDto.class) );
        }

        return returnValue;
    }

    @Override
    public AddressDto getAddress(String userId, String addressId) {

        UserEntity userEntity = userRepository.findByUserId(userId);
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        if (userEntity == null || addressEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + addressId);
        }

        AddressDto returnValue = modelMapper.map(addressEntity, AddressDto.class);

        return returnValue;
    }
}
