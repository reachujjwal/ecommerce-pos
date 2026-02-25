package com.ujjwal.service;

import com.ujjwal.exception.UserException;
import com.ujjwal.payload.dto.UserDTO;
import com.ujjwal.payload.response.AuthResponse;

public interface AuthService {
    AuthResponse login(String username, String password) throws UserException;
    AuthResponse signup(UserDTO req) throws UserException;

    void createPasswordResetToken(String email) throws UserException;
    void resetPassword(String token, String newPassword);
}
