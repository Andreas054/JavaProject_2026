package com.proiect.library.service;

import com.proiect.library.dto.AuthorDTO;
import com.proiect.library.dto.BookDTO;
import com.proiect.library.dto.GenreDTO;
import com.proiect.library.exception.BusinessException;
import com.proiect.library.exception.DuplicateResourceException;
import com.proiect.library.exception.ResourceNotFoundException;
import com.proiect.library.model.Author;
import com.proiect.library.model.Book;
import com.proiect.library.model.BookStatus;
import com.proiect.library.model.Genre;
import com.proiect.library.repository.AuthorRepository;
import com.proiect.library.repository.BookCopyRepository;
import com.proiect.library.repository.BookRepository;
import com.proiect.library.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookCopyRepository bookCopyRepository;

    public Page<BookDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return convertToDTO(book);
    }

    public BookDTO createBook(BookDTO bookDTO) {
        // Check if duplicate ISBN
        if (bookRepository.findByIsbn(bookDTO.getIsbn()).isPresent()) {
            throw new DuplicateResourceException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }

        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublicationYear(bookDTO.getPublicationYear());

        // set authors
        Set<Author> authors = new HashSet<>();
        for (Long authorId : bookDTO.getAuthorIds()) {
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
            authors.add(author);
        }
        book.setAuthors(authors);

        // set genres
        Set<Genre> genres = new HashSet<>();
        for (Long genreId : bookDTO.getGenreIds()) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId));
            genres.add(genre);
        }
        book.setGenres(genres);

        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // check duplicate ISBN (excluding current book)
        bookRepository.findByIsbn(bookDTO.getIsbn()).ifPresent(existingBook -> {
            if (!existingBook.getId().equals(id)) {
                throw new DuplicateResourceException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
            }
        });

        book.setTitle(bookDTO.getTitle());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublicationYear(bookDTO.getPublicationYear());

        // set authors
        Set<Author> authors = new HashSet<>();
        for (Long authorId : bookDTO.getAuthorIds()) {
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
            authors.add(author);
        }
        book.setAuthors(authors);

        // set genres
        Set<Genre> genres = new HashSet<>();
        for (Long genreId : bookDTO.getGenreIds()) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId));
            genres.add(genre);
        }
        book.setGenres(genres);

        Book updatedBook = bookRepository.save(book);
        return convertToDTO(updatedBook);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }

        // check if any copies are loaned
        long borrowedCopies = bookCopyRepository.countByBook_IdAndStatus(id, BookStatus.BORROWED);
        if (borrowedCopies > 0) {
            throw new BusinessException("Cannot delete book with active loans");
        }

        bookRepository.deleteById(id);
    }

    public List<BookDTO> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setPublicationYear(book.getPublicationYear());

        // authors DTO
        if (book.getAuthors() != null) {
            Set<AuthorDTO> authorDTOs = book.getAuthors().stream()
                    .map(author -> new AuthorDTO(author.getId(), author.getName()))
                    .collect(Collectors.toSet());
            dto.setAuthors(authorDTOs);
            dto.setAuthorIds(book.getAuthors().stream().map(Author::getId).collect(Collectors.toSet()));
        }

        // genres DTO
        if (book.getGenres() != null) {
            Set<GenreDTO> genreDTOs = book.getGenres().stream()
                    .map(genre -> new GenreDTO(genre.getId(), genre.getName()))
                    .collect(Collectors.toSet());
            dto.setGenres(genreDTOs);
            dto.setGenreIds(book.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));
        }

        // count available copies
        int availableCopies = (int) bookCopyRepository.countByBook_IdAndStatus(book.getId(), BookStatus.AVAILABLE);
        dto.setAvailableCopies(availableCopies);

        return dto;
    }
}
