package com.shopnest.controller;

import com.shopnest.entity.Address;
import com.shopnest.repository.AddressRepository;
import com.shopnest.repository.UserRepository;
import com.shopnest.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @PostMapping("/address")
    public ResponseEntity<Address> addAddress(Principal principal,
            @RequestBody Address address) {
        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        address.setUser(user);
        return ResponseEntity.ok(addressRepository.save(address));
    }

    @GetMapping("/addresses")
    public ResponseEntity<?> getAddresses(Principal principal) {
        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(addressRepository.findByUserId(user.getId()));
    }
}