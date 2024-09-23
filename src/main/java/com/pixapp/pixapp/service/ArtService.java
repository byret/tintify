package com.pixapp.pixapp.service;

import com.pixapp.pixapp.model.Art;
import com.pixapp.pixapp.repository.ArtRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtService {

    private final ArtRepository artRepository;

    public ArtService(ArtRepository artRepository) {
        this.artRepository = artRepository;
    }

    public List<Art> getAllPublicArts() {
        return artRepository.findByIsPublicTrue();
    }
}
