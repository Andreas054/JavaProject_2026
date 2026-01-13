package com.proiect.library.service;

import com.proiect.library.dto.BookCopyDTO;
import com.proiect.library.exception.ResourceNotFoundException;
import com.proiect.library.model.Book;
import com.proiect.library.model.BookCopy;
import com.proiect.library.model.BookStatus;
import com.proiect.library.repository.BookCopyRepository;
import com.proiect.library.repository.BookRepository;
import com.proiect.library.repository.LoanRepository;
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
public class BookCopyServiceTest {

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private BookCopyService bookCopyService;

    private Book book;
    private BookCopy bookCopy1;
    private BookCopy bookCopy2;
    private BookCopyDTO bookCopyDTO;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");

        bookCopy1 = new BookCopy();
        bookCopy1.setId(1L);
        bookCopy1.setBook(book);
        bookCopy1.setStatus(BookStatus.AVAILABLE);

        bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);
        bookCopy2.setBook(book);
        bookCopy2.setStatus(BookStatus.BORROWED);

        bookCopyDTO = new BookCopyDTO(1L, 1L, BookStatus.AVAILABLE, null);
    }

    @Test
    void testGetAllBookCopies() {
        given(bookCopyRepository.findAll()).willReturn(Arrays.asList(bookCopy1, bookCopy2));

        List<BookCopyDTO> result = bookCopyService.getAllBookCopies();

        assertEquals(2, result.size());
        verify(bookCopyRepository, times(1)).findAll();
    }

    @Test
    void testGetBookCopyById() {
        given(bookCopyRepository.findById(1L)).willReturn(Optional.of(bookCopy1));

        BookCopyDTO result = bookCopyService.getBookCopyById(1L);

        assertNotNull(result);
        assertEquals(BookStatus.AVAILABLE, result.getStatus());
    }

    @Test
    void testGetBookCopyById_NotFound() {
        given(bookCopyRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookCopyService.getBookCopyById(1L);
        });
    }

    @Test
    void testCreateBookCopy() {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        given(bookCopyRepository.save(any(BookCopy.class))).willReturn(bookCopy1);

        BookCopyDTO result = bookCopyService.createBookCopy(bookCopyDTO);

        assertNotNull(result);
        assertEquals(BookStatus.AVAILABLE, result.getStatus());
        verify(bookCopyRepository, times(1)).save(any(BookCopy.class));
    }

    @Test
    void testDeleteBookCopy() {
        given(bookCopyRepository.existsById(1L)).willReturn(true);
        given(loanRepository.existsByBookCopy_IdAndReturnDateIsNull(1L)).willReturn(false);
        doNothing().when(bookCopyRepository).deleteById(1L);

        bookCopyService.deleteBookCopy(1L);

        verify(bookCopyRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetBookCopiesByBookId() {
        given(bookRepository.existsById(1L)).willReturn(true);
        given(bookCopyRepository.findByBook_Id(1L)).willReturn(Arrays.asList(bookCopy1, bookCopy2));

        List<BookCopyDTO> result = bookCopyService.getBookCopiesByBookId(1L);

        assertEquals(2, result.size());
        verify(bookCopyRepository, times(1)).findByBook_Id(1L);
    }
}

