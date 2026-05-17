package com.shopnest.serviceImpl;

import com.shopnest.dto.request.LoginRequest;
import com.shopnest.dto.request.RegisterRequest;
import com.shopnest.dto.response.AuthResponse;
import com.shopnest.entity.Cart;
import com.shopnest.entity.User;
import com.shopnest.enums.Role;
import com.shopnest.exception.ApiException;
import com.shopnest.repository.CartRepository;
import com.shopnest.repository.UserRepository;
import com.shopnest.security.JwtUtil;
import com.shopnest.service.AuthService;
import com.shopnest.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.shopnest.security.CustomUserDetailsService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final EmailService emailService; // ✅ Added

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already registered: "
                    + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_CUSTOMER)
                .build();
        userRepository.save(user);

        Cart cart = Cart.builder().user(user).build();
        cartRepository.save(cart);

        // ✅ Send welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());

        UserDetails userDetails = userDetailsService
                .loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, user.getEmail(),
                user.getRole().name());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        UserDetails userDetails = userDetailsService
                .loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("User not found"));

        return new AuthResponse(token, user.getEmail(),
                user.getRole().name());
    }
}