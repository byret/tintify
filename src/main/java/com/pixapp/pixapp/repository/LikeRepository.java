package com.pixapp.pixapp.repository;

import com.pixapp.pixapp.model.Like;
import com.pixapp.pixapp.model.Palette;
import com.pixapp.pixapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPalette(User user, Palette palette);
}