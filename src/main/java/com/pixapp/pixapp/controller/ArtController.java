package com.pixapp.pixapp.controller;

import com.pixapp.pixapp.model.Art;
import com.pixapp.pixapp.model.User;
import com.pixapp.pixapp.repository.ArtRepository;
import com.pixapp.pixapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/arts")
public class ArtController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtRepository artRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createArt(@RequestBody Art artData, Authentication authentication) {
        Art art = new Art();
        art.setPixels(artData.getPixels());
        art.setPublic(artData.isPublic());
        art.setName(artData.getName());
        art.setWidth(artData.getWidth());
        art.setHeight(artData.getHeight());
        art.setPixelSize(artData.getPixelSize());
        art.setDownloadable(artData.isDownloadable());
        Art savedArt = artRepository.save(art);
        if (authentication != null) {
            Optional<User> user = userRepository.findByUsername(authentication.getName());
            art.setUser(user.orElse(null));
        } else {
            art.setUser(null);
        }

        artRepository.save(art);

        return ResponseEntity.ok(savedArt);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Art>> getArtsByUser(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        User user = userOptional.get();
        List<Art> publicArts = artRepository.findByUser(user);

        return ResponseEntity.ok(publicArts);
    }

    @GetMapping("/user/{username}/public")
    public ResponseEntity<List<Art>> getArtsByUserPublic(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        User user = userOptional.get();
        List<Art> publicArts = artRepository.findByUser(user).stream().filter(Art::isPublic).toList();

        return ResponseEntity.ok(publicArts);
    }

    @GetMapping("/public")
    public List<Art> getAllPublicArts() {
        return artRepository.findByIsPublicTrue();
    }
}