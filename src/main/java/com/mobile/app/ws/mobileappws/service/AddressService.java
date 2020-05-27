package com.mobile.app.ws.mobileappws.service;

import com.mobile.app.ws.mobileappws.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {

    List<AddressDto> getAddresses(String userId);
    AddressDto getAddress(String userId, String addressId);
}
