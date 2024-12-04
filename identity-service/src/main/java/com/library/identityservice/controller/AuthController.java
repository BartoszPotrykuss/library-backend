package com.library.identityservice.controller;

import com.library.identityservice.dto.AuthRequest;
import com.library.identityservice.entity.UserCredential;
import com.library.identityservice.repository.UserCredentialRepository;
import com.library.identityservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    private final UserCredentialRepository userCredentialRepository;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserCredentialRepository userCredentialRepository) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userCredentialRepository = userCredentialRepository;
    }

    @PostMapping("/register")
    public String addNewUser(@RequestBody UserCredential user) {
        return authService.saveUser(user);
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            // Pobranie roli z bazy danych
            String role = userCredentialRepository.findByName(authRequest.getUsername())
                    .map(UserCredential::getRole)
                    .orElse("USER"); // Domyślnie rola USER
            return authService.generateToken(authRequest.getUsername(), role); // Generowanie tokena z rolą
        } else {
            throw new RuntimeException("invalid access");
        }
    }


    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        authService.validateToken(token);
        return "Token is valid";
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<UserCredential>> getAllUsers() {
        List<UserCredential> users = authService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PatchMapping("/api/user/username/{username}/wallet")
    public ResponseEntity<UserCredential> patchUserWallet(@PathVariable("username") String username, @RequestBody Double fee) {
        UserCredential userCredential = authService.patchUserWallet(username, fee);
        return new ResponseEntity<>(userCredential, HttpStatus.OK);
    }

    @PatchMapping("/api/user/{username}/role")
    public ResponseEntity<UserCredential> patchUserRole(@PathVariable("username") String username , @RequestBody String newRole) {
        UserCredential userCredential = authService.patchUserRole(username, newRole);
        return new ResponseEntity<>(userCredential, HttpStatus.OK);
    }

    @GetMapping("/api/user/{username}/wallet")
    public ResponseEntity<Long> getUserWallet(@PathVariable("username") String username) {
        Long wallet = authService.getUserWallet(username);
        return new ResponseEntity<>(wallet, HttpStatus.OK);
    }

}
