package com.mobile.app.ws.mobileappws.security;

import com.mobile.app.ws.mobileappws.SpringApplicationContext;


public class SecurityConstants {

    public static final long EXPIRATION_TIME = 864000000; //10 days
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 360000; //1 hour
    public static final String TOKEN_PREFIX = "Bearer";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
    public static final String PASSWORD_REQUEST_EMAIL_URL= "/users/password-reset-request";
    public static final String PASSWORD_RESET_URL = "/users/password-reset";


    public static String getTokenSecret() {

        AppProperties appProperties = (AppProperties)SpringApplicationContext.getBean("appProperties");

        return appProperties.getTokenSecret();
    }
}
