package com.proiect.library.service;

import com.proiect.library.dto.GenreDTO;
import com.proiect.library.exception.DuplicateResourceException;
import com.proiect.library.exception.ResourceNotFoundException;
import com.proiect.library.model.Genre;
import com.proiect.library.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    private Genre genre1;
    private Genre genre2;
    private GenreDTO genreDTO;

    @BeforeEach
    void setUp() {
        genre1 = new Genre();
        genre1.setId(1L);
        genre1.setName("Genre1");

        genre2 = new Genre();
        genre2.setId(2L);
        genre2.setName("Genre2");

        genreDTO = new GenreDTO(1L, "Genre1");
    }

    @Test
    void testGetAllGenres() {
        given(genreRepository.findAll()).willReturn(Arrays.asList(genre1, genre2));

        List<GenreDTO> result = genreService.getAllGenres();

        assertEquals(2, result.size());
        verify(genreRepository, times(1)).findAll();
    }

    @Test
    void testGetGenreById() {
        given(genreRepository.findById(1L)).willReturn(Optional.of(genre1));

        GenreDTO result = genreService.getGenreById(1L);

        assertNotNull(result);
        assertEquals("Genre1", result.getName());
    }

    @Test
    void testGetGenreById_NotFound() {
        given(genreRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            genreService.getGenreById(1L);
        });
    }

    @Test
    void testCreateGenre() {
        given(genreRepository.findByName("Genre1")).willReturn(Optional.empty());
        given(genreRepository.save(any(Genre.class))).willReturn(genre1);

        GenreDTO result = genreService.createGenre(genreDTO);

        assertNotNull(result);
        assertEquals("Genre1", result.getName());
        verify(genreRepository, times(1)).save(any(Genre.class));
    }

    @Test
    void testCreateGenre_Duplicate() {
        given(genreRepository.findByName("Genre1")).willReturn(Optional.of(genre1));

        assertThrows(DuplicateResourceException.class, () -> {
            genreService.createGenre(genreDTO);
        });
    }

    @Test
    void testUpdateGenre() {
        given(genreRepository.findById(1L)).willReturn(Optional.of(genre1));
        given(genreRepository.findByName("Genre1")).willReturn(Optional.empty());
        given(genreRepository.save(any(Genre.class))).willReturn(genre1);

        GenreDTO result = genreService.updateGenre(1L, genreDTO);

        assertNotNull(result);
        assertEquals("Genre1", result.getName());
        verify(genreRepository, times(1)).save(any(Genre.class));
    }

    @Test
    void testDeleteGenre() {
        given(genreRepository.existsById(1L)).willReturn(true);
        doNothing().when(genreRepository).deleteById(1L);

        genreService.deleteGenre(1L);

        verify(genreRepository, times(1)).deleteById(1L);
    }
}

