package com.proiect.library.service;

import com.proiect.library.dto.LoanDTO;
import com.proiect.library.exception.BusinessException;
import com.proiect.library.exception.ResourceNotFoundException;
import com.proiect.library.model.Book;
import com.proiect.library.model.BookCopy;
import com.proiect.library.model.BookStatus;
import com.proiect.library.model.Loan;
import com.proiect.library.model.Reader;
import com.proiect.library.repository.BookCopyRepository;
import com.proiect.library.repository.LoanRepository;
import com.proiect.library.repository.ReaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private ReaderRepository readerRepository;

    @InjectMocks
    private LoanService loanService;

    private Reader reader;
    private Book book;
    private BookCopy bookCopy;
    private Loan loan1;
    private Loan loan2;
    private LoanDTO loanDTO;

    @BeforeEach
    void setUp() {
        reader = new Reader();
        reader.setId(1L);
        reader.setName("Reader Name");
        reader.setEmail("email@example.com");

        book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");

        bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setBook(book);
        bookCopy.setStatus(BookStatus.AVAILABLE);

        loan1 = new Loan();
        loan1.setId(1L);
        loan1.setBookCopy(bookCopy);
        loan1.setReader(reader);
        loan1.setBorrowDate(LocalDate.now());
        loan1.setDueDate(LocalDate.now().plusDays(14));
        loan1.setReturnDate(null);

        loan2 = new Loan();
        loan2.setId(2L);
        loan2.setBookCopy(bookCopy);
        loan2.setReader(reader);
        loan2.setBorrowDate(LocalDate.now().minusDays(20));
        loan2.setDueDate(LocalDate.now().minusDays(6));
        loan2.setReturnDate(null);

        loanDTO = new LoanDTO(1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(14), null, null, null, null);
    }

    @Test
    void testGetAllLoans() {
        given(loanRepository.findAll()).willReturn(Arrays.asList(loan1, loan2));

        List<LoanDTO> result = loanService.getAllLoans();

        assertEquals(2, result.size());
        verify(loanRepository, times(1)).findAll();
    }

    @Test
    void testGetLoanById() {
        given(loanRepository.findById(1L)).willReturn(Optional.of(loan1));

        LoanDTO result = loanService.getLoanById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetLoanById_NotFound() {
        given(loanRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            loanService.getLoanById(1L);
        });
    }

    @Test
    void testCreateLoan() {
        given(readerRepository.findById(1L)).willReturn(Optional.of(reader));
        given(bookCopyRepository.findById(1L)).willReturn(Optional.of(bookCopy));
        given(loanRepository.existsByBookCopy_IdAndReturnDateIsNull(1L)).willReturn(false);
        given(loanRepository.save(any(Loan.class))).willReturn(loan1);

        LoanDTO result = loanService.createLoan(loanDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void testCreateLoan_BookNotAvailable() {
        bookCopy.setStatus(BookStatus.BORROWED);
        given(readerRepository.findById(1L)).willReturn(Optional.of(reader));
        given(bookCopyRepository.findById(1L)).willReturn(Optional.of(bookCopy));

        assertThrows(BusinessException.class, () -> {
            loanService.createLoan(loanDTO);
        });
    }

    @Test
    void testReturnBook() {
        given(loanRepository.findById(1L)).willReturn(Optional.of(loan1));
        given(loanRepository.save(any(Loan.class))).willReturn(loan1);

        LoanDTO result = loanService.returnBook(1L, LocalDate.now());

        assertNotNull(result.getReturnDate());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void testDeleteLoan() {
        given(loanRepository.findById(1L)).willReturn(Optional.of(loan1));
        doNothing().when(loanRepository).deleteById(1L);

        loanService.deleteLoan(1L);

        verify(loanRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetLoansByReader() {
        given(readerRepository.existsById(1L)).willReturn(true);
        given(loanRepository.findByReader_Id(1L)).willReturn(Arrays.asList(loan1));

        List<LoanDTO> result = loanService.getLoansByReader(1L);

        assertEquals(1, result.size());
        verify(loanRepository, times(1)).findByReader_Id(1L);
    }

    @Test
    void testGetOverdueLoans() {
        given(loanRepository.findOverdueLoans(LocalDate.now())).willReturn(Arrays.asList(loan2));

        List<LoanDTO> result = loanService.getOverdueLoans();

        assertEquals(1, result.size());
        verify(loanRepository, times(1)).findOverdueLoans(LocalDate.now());
    }
}

