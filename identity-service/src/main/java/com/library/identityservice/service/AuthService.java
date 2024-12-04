package com.library.identityservice.service;

import com.library.identityservice.entity.UserCredential;
import com.library.identityservice.repository.UserCredentialRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public AuthService(UserCredentialRepository userCredentialRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userCredentialRepository = userCredentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String saveUser(UserCredential credential) {
        if (credential.getRole() == null || credential.getRole().isEmpty()) {
            credential.setRole("USER");
        }
        credential.setWallet(0L);
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        userCredentialRepository.save(credential);
        return "user added to the system";
    }


    public String generateToken(String username, String role) {
        return jwtService.generateToken(username, role);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    public List<UserCredential> getAllUsers() {
        return userCredentialRepository.findAll();
    }

    public UserCredential patchUserWallet(String username, Double fee) {
        UserCredential userCredential = userCredentialRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found with name: " + username));
        userCredential.setWallet((long) (userCredential.getWallet() + fee));
        return userCredentialRepository.save(userCredential);
    }

    public UserCredential patchUserRole(String username ,String newRole) {
        UserCredential userCredential = userCredentialRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found with name: " + username));
        userCredential.setRole(newRole);
        return userCredentialRepository.save(userCredential);
    }

    public Long getUserWallet(String username) {
        UserCredential userCredential = userCredentialRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found with name: " + username));
        return userCredential.getWallet();
    }
}
