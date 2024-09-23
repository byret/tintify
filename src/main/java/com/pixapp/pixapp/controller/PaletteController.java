package com.pixapp.pixapp.controller;

import com.pixapp.pixapp.model.Like;
import com.pixapp.pixapp.model.Palette;
import com.pixapp.pixapp.model.User;
import com.pixapp.pixapp.repository.LikeRepository;
import com.pixapp.pixapp.repository.PaletteRepository;
import com.pixapp.pixapp.repository.UserRepository;
import com.pixapp.pixapp.service.PaletteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/palettes")
public class PaletteController {

    @Autowired
    private PaletteRepository paletteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PaletteService paletteService;

    @PostMapping("/create")
    public ResponseEntity<?> createPalette(@RequestBody Palette paletteData, Authentication authentication) {
         Palette palette = new Palette();
         palette.setColors(paletteData.getColors());
         palette.setPublic(paletteData.isPublic());
         palette.setName(paletteData.getName());
         if (authentication != null) {
             Optional<User> user = userRepository.findByUsername(authentication.getName());
             palette.setUser(user.orElse(null));
         } else {
             palette.setUser(null);
         }

        paletteRepository.save(palette);

        return ResponseEntity.ok("Palette saved successfully");
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Palette>> getUserPalettes(@PathVariable String username) {
        // Находим пользователя по имени
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        User user = userOptional.get();
        List<Palette> userPalettes = paletteRepository.findByUser(user);

        return ResponseEntity.ok(userPalettes);
    }

    @GetMapping("/user/{username}/public")
    public ResponseEntity<List<Palette>> getUserPalettesPublic(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        User user = userOptional.get();
        List<Palette> userPalettes = paletteRepository.findByUser(user).stream().filter(Palette::isPublic).toList();

        return ResponseEntity.ok(userPalettes);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id, Authentication authentication) {
        Optional<Palette> optionalPalette = paletteRepository.findById(id);
        if (optionalPalette.isPresent()) {
            Palette palette = optionalPalette.get();
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);

            if (user == null) {
                return ResponseEntity.badRequest().body("User not found.");
            }

            Optional<Like> existingLike = likeRepository.findByUserAndPalette(user, palette);

            if (existingLike.isPresent()) {
                likeRepository.delete(existingLike.get());
                palette.setLikes(palette.getLikes() - 1);
            } else {
                Like like = new Like();
                like.setUser(user);
                like.setPalette(palette);
                likeRepository.save(like);
                palette.setLikes(palette.getLikes() + 1);
            }

            paletteRepository.save(palette);

            return ResponseEntity.ok("Like toggled successfully");
        } else {
            return ResponseEntity.badRequest().body("Palette not found.");
        }
    }

    @GetMapping("/user/{username}/likes")
    public ResponseEntity<List<Palette>> getLikedPalettes(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        User user = userOptional.get();
        List<Palette> likedPalettes = paletteRepository.findLikedPalettesByUser(user);

        return ResponseEntity.ok(likedPalettes);
    }

    @GetMapping("/user/{username}/likes/public")
    public ResponseEntity<List<Palette>> getLikedPalettesPublic(@PathVariable String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        User user = userOptional.get();
        List<Palette> likedPalettes = paletteRepository.findLikedPalettesByUser(user).stream().filter(Palette::isPublic).toList();

        return ResponseEntity.ok(likedPalettes);
    }

    @GetMapping("/public")
    public ResponseEntity<List<Palette>> getAllPublicPalettes() {
        List<Palette> publicPalettes = paletteService.getAllPublicPalettes();
        return ResponseEntity.ok(publicPalettes);
    }

    @GetMapping("/{id}/likes/users")
    public ResponseEntity<List<User>> getUsersWhoLikedPalette(@PathVariable Long id) {
        List<User> usersWhoLiked = paletteRepository.findUsersWhoLikedPalette(id);
        if (usersWhoLiked.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usersWhoLiked);
    }
}
