package com.ujjwal.mapper;

import com.ujjwal.modal.User;
import com.ujjwal.payload.response.AuthResponse;

public class AuthResponseMapper {

    public static AuthResponse toDto(User user, String jwt) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setUser(UserMapper.toDTO(user));

        return authResponse;
    }
}
