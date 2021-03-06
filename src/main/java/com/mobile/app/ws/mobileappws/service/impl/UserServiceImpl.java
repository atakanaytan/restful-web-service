package com.mobile.app.ws.mobileappws.service.impl;

import com.mobile.app.ws.mobileappws.exceptions.UserServiceException;
import com.mobile.app.ws.mobileappws.io.entity.PasswordResetTokenEntity;
import com.mobile.app.ws.mobileappws.io.entity.UserEntity;
import com.mobile.app.ws.mobileappws.io.repository.UserRepository;
import com.mobile.app.ws.mobileappws.io.repository.PasswordResetTokenRepository;
import com.mobile.app.ws.mobileappws.service.UserService;
import com.mobile.app.ws.mobileappws.shared.AmazonSES;
import com.mobile.app.ws.mobileappws.shared.Utils;
import com.mobile.app.ws.mobileappws.shared.dto.AddressDto;
import com.mobile.app.ws.mobileappws.shared.dto.UserDto;
import com.mobile.app.ws.mobileappws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
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
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Utils utils;

    @Override
    public UserDto createUser(UserDto user) {

        UserEntity userEntity = userRepository.findUserByEmail(user.getEmail());

        if (userEntity != null) {
            throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        }

        for (int i = 0; i < user.getAddresses().size(); i++) {

            AddressDto address = user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i, address);
        }

        UserEntity newUser = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        newUser.setUserId(publicUserId);
        newUser.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        newUser.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        newUser.setEmailVerificationStatus(false);

        UserEntity storedUserDetails = userRepository.save(newUser);

        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        //Send email message to user verify their email address
        new AmazonSES().verifyEmail(returnValue);

        return returnValue;
    }

    @Override
    public UserDto getUser(String email) {

        UserEntity userEntity = userRepository.findUserByEmail(email);

        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.EMAIL_ADDRESS_NOT_VERIFIED.getErrorMessage() + email);
        }

        UserDto returnValue = modelMapper.map(userEntity, UserDto.class);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        UserEntity userEntity = userRepository.findUserByEmail(email);

        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.EMAIL_ADDRESS_NOT_VERIFIED.getErrorMessage() + email);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(),
                true, true,
                true, new ArrayList<>());
    }

    @Override
    public UserDto getUserByUserId(String userId) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + userId);
        }

        UserDto returnValue = modelMapper.map(userEntity, UserDto.class);

        return returnValue;
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + userId);
        }

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(updatedUserDetails, UserDto.class);

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

        returnValue = mapUserEntityObjectToUserDto(users, returnValue);

        return returnValue;
    }

    @Override
    public boolean verifyEmailToken(String token) {

        boolean returnValue = false;

        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if (userEntity != null) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }

        return returnValue;
    }

    @Override
    public boolean requestPasswordReset(String email) {

        boolean returnValue = false;

        UserEntity userEntity = userRepository.findUserByEmail(email);

        if (userEntity == null) {
            return returnValue;
        }

        String token = new Utils().generatePasswordResetToken(userEntity.getUserId());

        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();

        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setUserDetails(userEntity);

        passwordResetTokenRepository.save(passwordResetTokenEntity);

        returnValue = new AmazonSES().sendPasswordResetRequest(
                userEntity.getFirstName(),
                userEntity.getEmail(),
                token);

        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String password) {

        boolean returnValue = false;

        if (Utils.hasTokenExpired(token)) {
            return returnValue;
        }

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenEntity == null) {

            return returnValue;
        }

        // Prepare new password
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // Update User password in database
        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
        userEntity.setEncryptedPassword(encodedPassword);

        UserEntity savedUserEntity = userRepository.save(userEntity);

        // Verify if password was saved successfully
        if (savedUserEntity != null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) {
            returnValue = true;
        }

        // Remove the password reset token from database
        passwordResetTokenRepository.delete(passwordResetTokenEntity);

        return returnValue;
    }


    private List<UserDto> mapUserEntityObjectToUserDto(List<UserEntity> users, List<UserDto> returnValue) {

        for (UserEntity userEntity : users){
            UserDto userDto = modelMapper.map(userEntity, UserDto.class);
            returnValue.add(userDto);
        }

        return returnValue;
    }

}
