package com.mobile.app.ws.mobileappws.ui.model.request;


import javax.validation.constraints.NotEmpty;

public class UserDetailsRequestModel {

    @NotEmpty(message = "Please enter the first name")
    private String firstName;

    @NotEmpty(message = "Please enter the last name")
    private String lastName;

    @NotEmpty(message = "Please enter the email")
    private String email;

    @NotEmpty(message = "Please enter the password")
    private String password;


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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
