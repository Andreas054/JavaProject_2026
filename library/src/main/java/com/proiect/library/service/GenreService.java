package com.proiect.library.service;

import com.proiect.library.dto.GenreDTO;
import com.proiect.library.exception.DuplicateResourceException;
import com.proiect.library.exception.ResourceNotFoundException;
import com.proiect.library.model.Genre;
import com.proiect.library.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GenreService {
    private final GenreRepository genreRepository;

    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<GenreDTO> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable).map(this::convertToDTO);
    }

    public GenreDTO getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));
        return convertToDTO(genre);
    }

    public GenreDTO createGenre(GenreDTO genreDTO) {
        // check duplicate name
        if (genreRepository.findByName(genreDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("Genre with name " + genreDTO.getName() + " already exists");
        }

        Genre genre = new Genre();
        genre.setName(genreDTO.getName());

        Genre savedGenre = genreRepository.save(genre);
        return convertToDTO(savedGenre);
    }

    public GenreDTO updateGenre(Long id, GenreDTO genreDTO) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));

        // check duplicate name (excluding current genre)
        genreRepository.findByName(genreDTO.getName()).ifPresent(existingGenre -> {
            if (!existingGenre.getId().equals(id)) {
                throw new DuplicateResourceException("Genre with name " + genreDTO.getName() + " already exists");
            }
        });

        genre.setName(genreDTO.getName());
        Genre updatedGenre = genreRepository.save(genre);
        return convertToDTO(updatedGenre);
    }

    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Genre not found with id: " + id);
        }
        genreRepository.deleteById(id);
    }

    private GenreDTO convertToDTO(Genre genre) {
        return new GenreDTO(genre.getId(), genre.getName());
    }
}
