package com.pixapp.pixapp.repository;

import com.pixapp.pixapp.model.Art;
import com.pixapp.pixapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArtRepository extends JpaRepository<Art, Long> {
    List<Art> findByUser(User user);
    List<Art> findByIsPublicTrue();
}