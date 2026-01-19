package com.bk.ems3.service;

import com.bk.ems3.dto.LoginDTO;
import com.bk.ems3.dto.LoginResponse;
import org.springframework.stereotype.Service;
import com.bk.ems3.dto.RegisterDTO;
import com.bk.ems3.model.Role;
import com.bk.ems3.model.User;
import com.bk.ems3.model.UserStatus;
import com.bk.ems3.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import com.bk.ems3.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void register(RegisterDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.EMPLOYEE);
        user.setStatus(UserStatus.PENDING);
        user.setFirstLogin(false);

        userRepository.save(user);
    }

    public LoginResponse login(LoginDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            throw new RuntimeException("Your account is pending approval");
        }

        if (user.getStatus() == UserStatus.REJECTED) {
            throw new RuntimeException("Your account has been rejected");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(
                token,
                user.getRole().name(),
                user.isFirstLogin()
        );
    }

}
