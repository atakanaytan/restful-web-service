package com.mobile.app.ws.mobileappws.ui.model.request;

import javax.validation.constraints.NotEmpty;

public class UserUpdateDetailsRequestModel {


    @NotEmpty(message = "Please enter the first name")
    private String firstName;

    @NotEmpty(message = "Please enter the last name")
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
