package com.pixapp.pixapp.service;

import com.pixapp.pixapp.model.Palette;
import com.pixapp.pixapp.repository.PaletteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaletteService {

    private final PaletteRepository paletteRepository;

    public PaletteService(PaletteRepository paletteRepository) {
        this.paletteRepository = paletteRepository;
    }

    public List<Palette> getAllPublicPalettes() {
        return paletteRepository.findByIsPublicTrue();
    }
}
