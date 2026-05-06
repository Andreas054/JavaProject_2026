package com.proiect.library.service;

import com.proiect.library.dto.AuthorDTO;
import com.proiect.library.exception.DuplicateResourceException;
import com.proiect.library.exception.ResourceNotFoundException;
import com.proiect.library.model.Author;
import com.proiect.library.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author author1;
    private Author author2;
    private AuthorDTO authorDTO;

    @BeforeEach
    void setUp() {
        author1 = new Author();
        author1.setId(1L);
        author1.setName("Author1");

        author2 = new Author();
        author2.setId(2L);
        author2.setName("Author2");

        authorDTO = new AuthorDTO(1L, "Author1");
    }

    @Test
    void testGetAllAuthors() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Author> authors = Arrays.asList(author1, author2);
        Page<Author> authorPage = new PageImpl<>(authors, pageable, authors.size());

        given(authorRepository.findAll(pageable)).willReturn(authorPage);

        Page<AuthorDTO> result = authorService.getAllAuthors(pageable);

        assertEquals(2, result.getTotalElements());
        verify(authorRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAuthorById() {
        given(authorRepository.findById(1L)).willReturn(Optional.of(author1));

        AuthorDTO result = authorService.getAuthorById(1L);

        assertNotNull(result);
        assertEquals("Author1", result.getName());
    }

    @Test
    void testGetAuthorById_NotFound() {
        given(authorRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.getAuthorById(1L));
    }

    @Test
    void testCreateAuthor() {
        given(authorRepository.findByName("Author1")).willReturn(Optional.empty());
        given(authorRepository.save(any(Author.class))).willReturn(author1);

        AuthorDTO result = authorService.createAuthor(authorDTO);

        assertNotNull(result);
        assertEquals("Author1", result.getName());
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testCreateAuthor_Duplicate() {
        given(authorRepository.findByName("Author1")).willReturn(Optional.of(author1));

        assertThrows(DuplicateResourceException.class, () -> authorService.createAuthor(authorDTO));
    }

    @Test
    void testUpdateAuthor() {
        given(authorRepository.findById(1L)).willReturn(Optional.of(author1));
        given(authorRepository.findByName("Author1")).willReturn(Optional.empty());
        given(authorRepository.save(any(Author.class))).willReturn(author1);

        AuthorDTO result = authorService.updateAuthor(1L, authorDTO);

        assertNotNull(result);
        assertEquals("Author1", result.getName());
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testDeleteAuthor() {
        given(authorRepository.existsById(1L)).willReturn(true);
        doNothing().when(authorRepository).deleteById(1L);

        authorService.deleteAuthor(1L);

        verify(authorRepository, times(1)).deleteById(1L);
    }
}
