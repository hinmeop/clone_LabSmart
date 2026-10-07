package com.dev.smartlabbackend.service;

import com.dev.smartlabbackend.dto.request.LoginRequest;
import com.dev.smartlabbackend.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void logout(String bearerToken);
}
