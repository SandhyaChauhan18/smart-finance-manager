package com.smartfinance.smart_finance_manager.controller;

import com.smartfinance.smart_finance_manager.dto.LoginRequest;
import com.smartfinance.smart_finance_manager.model.User;
import com.smartfinance.smart_finance_manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import com.smartfinance.smart_finance_manager.dto.UserProfileDTO;


@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/user/upload-photo")
    public ResponseEntity<?> uploadProfilePhoto(@RequestParam("email") String email,
                                                @RequestParam("photo") MultipartFile photo) {

        User user = userService.getUserByEmail(email);
        if (user == null) return ResponseEntity.badRequest().body("User not found");

        try {
            String contentType = photo.getContentType();
            if (contentType == null ||
                    !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg"))) {
                return ResponseEntity.badRequest().body("Only JPG, JPEG, and PNG images are allowed.");
            }

            String uploadDir = new File("uploads").getAbsolutePath();
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
            File dest = new File(dir, fileName);
            photo.transferTo(dest);

            user.setProfilePhoto("uploads/" + fileName);
            userService.saveUser(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Photo uploaded successfully",
                    "photoUrl", "http://localhost:8080/uploads/" + fileName
            ));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest request) {
        String response = userService.loginUser(request.getEmail(), request.getPassword());
        if (response.equals("Login successful!")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        UserProfileDTO profileDTO = new UserProfileDTO(user);
        return ResponseEntity.ok(profileDTO);
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUserProfile(@RequestParam("name") String name,
                                               @RequestParam("phoneNumber") String phoneNumber,
                                               @RequestParam("address") String address,
                                               @RequestParam(value = "photo", required = false) MultipartFile photo,
                                               Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);

        if (photo != null && !photo.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
                File dest = new File(uploadDir + fileName);
                photo.transferTo(dest);

                user.setProfilePhoto("uploads/" + fileName);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Photo upload failed: " + e.getMessage());
            }
        }

        userService.saveUser(user);
        return ResponseEntity.ok("Profile updated successfully!");
    }


}

