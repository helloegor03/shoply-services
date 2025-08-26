package com.helloegor03.auth_service.service;

import com.helloegor03.auth_service.config.JwtUtil;
import com.helloegor03.auth_service.dto.AuthRequest;
import com.helloegor03.auth_service.dto.RegisterRequest;
import com.helloegor03.auth_service.repository.UserRepository;

import com.helloegor03.auth_service.user.User;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void deleteUser(Long id){
        if(userRepository.findById(id).isEmpty()){
            throw new RuntimeException("Not found user with this id");
        }
        userRepository.deleteById(id);
    }

    private Authentication authenticateUser(AuthRequest authRequest) throws AuthenticationException {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    public void register(RegisterRequest registerRequest){
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("User with this username already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        userRepository.save(user);
    }


    public String login(AuthRequest authRequest){
        Authentication authentication = authenticateUser(authRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtil.generateToken(authentication);

    }


}
