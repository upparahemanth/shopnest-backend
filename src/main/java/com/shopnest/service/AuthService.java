package com.shopnest.service;

import com.shopnest.dto.request.LoginRequest;
import com.shopnest.dto.request.RegisterRequest;
import com.shopnest.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}