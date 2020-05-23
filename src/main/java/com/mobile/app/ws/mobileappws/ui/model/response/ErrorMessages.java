package com.mobile.app.ws.mobileappws.ui.model.response;

public enum ErrorMessages {

    MISSING_REQUIRED_FIELD("Missing required field. Please check documentation for required fields"),
    RECORD_ALREADY_EXISTS("Record already exists"),
    INTERNAL_SERVER_ERROR("Internal server error"),
    NO_RECORD_FOUND("Record is not found with provided id: "),
    AUTHENTICATION_FAILED("Authentication failed"),
    COULD_NOT_UPDATE_RECORD("Could not update due to existing email"),
    COULD_NOT_DELETE_RECORD("Could not delete record with id: "),
    EMAIL_ADDRESS_NOT_VERIFIED("Email address could not verified with given email: ");

    private String errorMessage;

    ErrorMessages(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
