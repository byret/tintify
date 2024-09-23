package com.pixapp.pixapp.repository;

import com.pixapp.pixapp.model.Palette;
import com.pixapp.pixapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaletteRepository extends JpaRepository<Palette, Long> {
    List<Palette> findByUser(User user);
    @Query("SELECT p FROM Palette p JOIN Like l ON p.id = l.palette.id WHERE l.user = :user")
    List<Palette> findLikedPalettesByUser(@Param("user") User user);
    List<Palette> findByIsPublicTrue();
    @Query("SELECT l.user FROM Like l WHERE l.palette.id = :paletteId")
    List<User> findUsersWhoLikedPalette(@Param("paletteId") Long paletteId);
}
