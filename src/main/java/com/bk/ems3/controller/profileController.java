package com.bk.ems3.controller;

import com.bk.ems3.dto.ChangePasswordDTO;
import com.bk.ems3.dto.UpdateProfileDTO;
import com.bk.ems3.model.User;
import com.bk.ems3.repository.UserRepository;
import com.bk.ems3.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class profileController {

    private final UserRepository userRepository;
    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {

        System.out.println("🔍 /me API called");

        if (authentication == null) {
            System.out.println("❌ Authentication is null");
            return ResponseEntity.status(401).build();
        }

        User loggedInUser = (User) authentication.getPrincipal();

        System.out.println("✅ Logged in user: " + loggedInUser.getEmail());
        System.out.println("✅ Role: " + loggedInUser.getRole());

        return ResponseEntity.ok(loggedInUser);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            @RequestBody UpdateProfileDTO dto,
            Authentication authentication
    ) {
        System.out.println("✏️ [DEBUG] PUT /me called");

        User loggedInUser = (User) authentication.getPrincipal();
        String email = loggedInUser.getEmail();
        System.out.println("🔑 Logged in user: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ only allowed fields
        user.setFullName(dto.getFullName());
        user.setDepartment(dto.getDepartment());
        user.setDesignation(dto.getDesignation());

        userRepository.save(user);

        System.out.println("✅ Profile updated for: " + email);

        return ResponseEntity.ok("Profile updated successfully");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordDTO dto
    ) {
        User user = (User) authentication.getPrincipal();

        profileService.changePassword(user, dto);

        return ResponseEntity.ok("Password changed successfully");
    }


}
