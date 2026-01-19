package com.bk.ems3.service;

import com.bk.ems3.dto.ChangePasswordDTO;
import com.bk.ems3.model.User;
import com.bk.ems3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(User loggedInUser, ChangePasswordDTO dto) {

        // ✅ Validate old password
        if (!passwordEncoder.matches(dto.getOldPassword(), loggedInUser.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // ❌ Prevent same password
        if (passwordEncoder.matches(dto.getNewPassword(), loggedInUser.getPassword())) {
            throw new RuntimeException("New password cannot be same as old password");
        }

        // ✅ Update password
        loggedInUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        // ✅ VERY IMPORTANT
        loggedInUser.setFirstLogin(false);

        userRepository.save(loggedInUser);
    }
}
