package com.mobile.app.ws.mobileappws.ui.controller;

import com.mobile.app.ws.mobileappws.service.validation.MapValidationErrorService;
import com.mobile.app.ws.mobileappws.service.UserService;
import com.mobile.app.ws.mobileappws.shared.dto.UserDto;
import com.mobile.app.ws.mobileappws.ui.model.request.UserDetailsRequestModel;
import com.mobile.app.ws.mobileappws.ui.model.request.UserUpdateDetailsRequestModel;
import com.mobile.app.ws.mobileappws.ui.model.response.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;


    @GetMapping(path="/{user_id}")
    public ResponseEntity<?> getUser(@PathVariable String user_id){

        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(user_id);
        BeanUtils.copyProperties(userDto, returnValue);

        return new ResponseEntity<UserRest>(returnValue, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDetailsRequestModel userDetails,
                                        BindingResult result ){

        UserRest returnValue = new UserRest();

        ResponseEntity<?> errorMap =  mapValidationErrorService.MapValidationService(result);
        if(errorMap != null){
            return errorMap;
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);

        return new ResponseEntity<UserRest>(returnValue, HttpStatus.CREATED);
    }


    @PatchMapping(path= "/{user_id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDetailsRequestModel userUpdatedDetails,
                                        BindingResult result, @PathVariable String user_id ){

        ResponseEntity<?> errorMap =  mapValidationErrorService.MapValidationService(result);
        if(errorMap != null){
            return errorMap;
        }

        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userUpdatedDetails, userDto);

        UserDto updateUser = userService.updateUser(user_id, userDto);
        BeanUtils.copyProperties(updateUser, returnValue);

        return new ResponseEntity<UserRest>(returnValue, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable String user_id) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(user_id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return new ResponseEntity<OperationStatusModel>(returnValue, HttpStatus.OK);

    }
}
