package com.mobile.app.ws.mobileappws.ui.controller;

import com.mobile.app.ws.mobileappws.service.AddressService;
import com.mobile.app.ws.mobileappws.service.validation.MapValidationErrorService;
import com.mobile.app.ws.mobileappws.service.UserService;
import com.mobile.app.ws.mobileappws.shared.dto.AddressDto;
import com.mobile.app.ws.mobileappws.shared.dto.UserDto;
import com.mobile.app.ws.mobileappws.ui.model.request.UserDetailsRequestModel;
import com.mobile.app.ws.mobileappws.ui.model.request.UserUpdateDetailsRequestModel;
import com.mobile.app.ws.mobileappws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(path="/{user_id}")
    public ResponseEntity<?> getUser(@PathVariable String user_id){

        UserDto userDto = userService.getUserByUserId(user_id);
        UserRest returnValue = modelMapper.map(userDto, UserRest.class);

        return new ResponseEntity<UserRest>(returnValue, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDetailsRequestModel userDetails,
                                        BindingResult result ) throws Exception{

        ResponseEntity<?> errorMap =  mapValidationErrorService.MapValidationService(result);
        if(errorMap != null){
            return errorMap;
        }

        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        UserRest returnValue = modelMapper.map(createdUser, UserRest.class);

        return new ResponseEntity<UserRest>(returnValue, HttpStatus.CREATED);
    }

    @PatchMapping(path= "/{user_id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDetailsRequestModel userUpdatedDetails,
                                        BindingResult result, @PathVariable String user_id ){

        ResponseEntity<?> errorMap =  mapValidationErrorService.MapValidationService(result);
        if(errorMap != null){
            return errorMap;
        }

        UserDto userDto = modelMapper.map(userUpdatedDetails, UserDto.class);

        UserDto  updateUser  = userService.updateUser(user_id, userDto);
        UserRest returnValue = modelMapper.map(updateUser, UserRest.class);

        return new ResponseEntity<UserRest>(returnValue, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable String user_id) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(ErrorMessage.RequestOperationName.DELETE.name());

        userService.deleteUser(user_id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return new ResponseEntity<OperationStatusModel>(returnValue, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "limit", defaultValue = "25") int limit) {

        List<UserRest> returnValue = new ArrayList<UserRest>();

        List<UserDto> users = userService.getUsers(page, limit);

        returnValue = mapUserDtoObjectAsUserRest(users, returnValue);

        return new ResponseEntity<List<UserRest>>(returnValue, HttpStatus.OK);
    }

    @GetMapping(path = "/{user_id}/addresses")
    public ResponseEntity<?> getUserAddresses(@PathVariable String user_id){

        List<AddressesRest> returnValue  = new ArrayList<>();

        List<AddressDto> addressDto = addressService.getAddresses(user_id);

        if (addressDto != null && !addressDto.isEmpty()) {
            java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            returnValue = modelMapper.map(addressDto, listType);
        }

        return new ResponseEntity<List<AddressesRest>>(returnValue, HttpStatus.OK);
    }

    @GetMapping(path = "/{user_id}/addresses/{address_id}")
    public ResponseEntity<?> getUserAddress(@PathVariable String user_id, @PathVariable String address_id) {

        AddressDto addressDto = addressService.getAddress(user_id, address_id);

        ModelMapper modelMapper = new ModelMapper();

        AddressesRest returnValue = modelMapper.map(addressDto, AddressesRest.class);

        return new ResponseEntity<AddressesRest>(returnValue, HttpStatus.OK);
    }

    @GetMapping(path = "/email-verification")
    public ResponseEntity<?> verifyEmailToken(@RequestParam(value = "token") String token) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(ErrorMessage.RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return new ResponseEntity<OperationStatusModel>(returnValue, HttpStatus.OK);
    }




    private List<UserRest> mapUserDtoObjectAsUserRest(List<UserDto> users, List<UserRest> returnValue) {

        for (UserDto userDto : users) {
             UserRest userModel = modelMapper.map(userDto, UserRest.class);
             returnValue.add(userModel);
        }

        return returnValue;
    }
}
