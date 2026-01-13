package com.proiect.library.service;

import com.proiect.library.dto.BookDTO;
import com.proiect.library.exception.DuplicateResourceException;
import com.proiect.library.exception.ResourceNotFoundException;
import com.proiect.library.model.Author;
import com.proiect.library.model.Book;
import com.proiect.library.model.Genre;
import com.proiect.library.repository.AuthorRepository;
import com.proiect.library.repository.BookCopyRepository;
import com.proiect.library.repository.BookRepository;
import com.proiect.library.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;
    private BookDTO bookDTO;
    private Author author;
    private Genre genre;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setName("Author Name");

        genre = new Genre();
        genre.setId(1L);
        genre.setName("Genre Name");

        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Title1");
        book1.setIsbn("1234567890");
        book1.setPublicationYear(2022);
        book1.setAuthors(new HashSet<>(Arrays.asList(author)));
        book1.setGenres(new HashSet<>(Arrays.asList(genre)));

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Title2");
        book2.setIsbn("0987654321");
        book2.setPublicationYear(2021);

        Set<Long> authorIds = new HashSet<>(Arrays.asList(1L));
        Set<Long> genreIds = new HashSet<>(Arrays.asList(1L));
        bookDTO = new BookDTO(1L, "Title1", "1234567890", 2022, authorIds, genreIds, null, null, null);
    }

    @Test
    void testGetAllBooks() {
        given(bookRepository.findAll()).willReturn(Arrays.asList(book1, book2));

        List<BookDTO> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBookById() {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book1));

        BookDTO result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals("Title1", result.getTitle());
    }

    @Test
    void testGetBookById_NotFound() {
        given(bookRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.getBookById(1L);
        });
    }

    @Test
    void testCreateBook() {
        given(bookRepository.findByIsbn(anyString())).willReturn(Optional.empty());
        given(authorRepository.findById(anyLong())).willReturn(Optional.of(author));
        given(genreRepository.findById(anyLong())).willReturn(Optional.of(genre));
        given(bookRepository.save(any(Book.class))).willReturn(book1);

        BookDTO result = bookService.createBook(bookDTO);

        assertNotNull(result);
        assertEquals("Title1", result.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testCreateBook_DuplicateIsbn() {
        given(bookRepository.findByIsbn(anyString())).willReturn(Optional.of(book1));

        assertThrows(DuplicateResourceException.class, () -> {
            bookService.createBook(bookDTO);
        });
    }

    @Test
    void testUpdateBook() {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book1));
        given(bookRepository.findByIsbn(anyString())).willReturn(Optional.empty());
        given(authorRepository.findById(anyLong())).willReturn(Optional.of(author));
        given(genreRepository.findById(anyLong())).willReturn(Optional.of(genre));
        given(bookRepository.save(any(Book.class))).willReturn(book1);

        BookDTO result = bookService.updateBook(1L, bookDTO);

        assertNotNull(result);
        assertEquals("Title1", result.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testDeleteBook() {
        given(bookRepository.existsById(1L)).willReturn(true);
        given(bookCopyRepository.countByBook_IdAndStatus(1L, com.proiect.library.model.BookStatus.BORROWED)).willReturn(0L);
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSearchBooksByTitle() {
        given(bookRepository.findByTitleContainingIgnoreCase("Title")).willReturn(Arrays.asList(book1, book2));

        List<BookDTO> result = bookService.searchBooksByTitle("Title");

        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase("Title");
    }
}

