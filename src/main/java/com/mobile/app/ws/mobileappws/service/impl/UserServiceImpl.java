package com.mobile.app.ws.mobileappws.service.impl;

import com.mobile.app.ws.mobileappws.exceptions.UserServiceException;
import com.mobile.app.ws.mobileappws.io.entity.UserEntity;
import com.mobile.app.ws.mobileappws.io.repository.UserRepository;
import com.mobile.app.ws.mobileappws.service.UserService;
import com.mobile.app.ws.mobileappws.shared.Utils;
import com.mobile.app.ws.mobileappws.shared.dto.AddressDto;
import com.mobile.app.ws.mobileappws.shared.dto.UserDto;
import com.mobile.app.ws.mobileappws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {

        if (userRepository.findUserByEmail(user.getEmail()) != null) {
            throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        }

        for (int i=0; i<user.getAddresses().size(); i++) {

            AddressDto address = user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i, address);
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        return returnValue;
    }

    @Override
    public UserDto getUser(String email) {

        UserEntity userEntity = userRepository.findUserByEmail(email);

        if (userEntity == null){
            throw new UserServiceException(ErrorMessages.EMAIL_ADDRESS_NOT_VERIFIED.getErrorMessage() + email);
        }

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        UserEntity userEntity = userRepository.findUserByEmail(email);

        if (userEntity == null){
            throw new UserServiceException(ErrorMessages.EMAIL_ADDRESS_NOT_VERIFIED.getErrorMessage() + email);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }

    @Override
    public UserDto getUserByUserId(String userId) {

        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null){
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + userId);
        }

        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + userId);
        }

        UserDto returnValue = new UserDto();

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails = userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUserDetails, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.COULD_NOT_DELETE_RECORD.getErrorMessage() + userId);
        }

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {

        List<UserDto> returnValue = new ArrayList<UserDto>();

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        returnValue = copyUserDtoAsUserEntity(users, returnValue);

        return returnValue;
    }

    private List<UserDto> copyUserDtoAsUserEntity(List<UserEntity> users, List<UserDto> returnValue) {

        for (UserEntity userEntity : users){
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }

}
