package com.pixapp.pixapp.controller;

import com.pixapp.pixapp.model.User;
import com.pixapp.pixapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setAvatarPath("/default_avatar.png");

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/update-user-details")
    public ResponseEntity<?> updateUserDetails(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("newUsername") String newUsername,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam(value = "oldPassword", required = false) String oldPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword) {

        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!loggedInUsername.equals(currentUsername)) {
            return ResponseEntity.status(403).body("You are not authorized to update this user.");
        }

        Optional<User> userOptional = userRepository.findByUsername(currentUsername);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        User user = userOptional.get();

        if (newPassword != null && oldPassword != null) {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ResponseEntity.status(400).body("Incorrect old password.");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
        }

        if (!currentUsername.equals(newUsername)) {
            if (userRepository.findByUsername(newUsername).isPresent()) {
                return ResponseEntity.status(400).body("Username already taken.");
            }
            user.setUsername(newUsername);
        }

        if (avatar != null && !avatar.isEmpty()) {
            try {
                Path avatarDirectory = Paths.get("avatars/");
                Files.createDirectories(avatarDirectory);

                Path avatarPath = avatarDirectory.resolve(newUsername + ".png");
                Files.write(avatarPath, avatar.getBytes());

                user.setAvatarPath("/avatars/" + newUsername + ".png");

            } catch (IOException e) {
                return ResponseEntity.status(500).body("Failed to upload avatar.");
            }
        }

        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "avatarPath", user.getAvatarPath()
        ));
    }

    @GetMapping("/edit/{username}")
    public ResponseEntity<?> getUserDetails(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }
        User user = userOptional.get();

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "avatarPath", user.getAvatarPath()
        ));
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getPublicUserProfile(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        User user = userOptional.get();
        Map<String, String> publicInfo = Map.of(
                "username", user.getUsername(),
                "avatarPath", user.getAvatarPath() != null ? user.getAvatarPath() : ""
        );

        return ResponseEntity.ok(publicInfo);
    }

}
