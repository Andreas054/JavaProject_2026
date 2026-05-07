package com.proiect.library.service;

import com.proiect.library.dto.LoanDTO;
import com.proiect.library.exception.BusinessException;
import com.proiect.library.exception.ResourceNotFoundException;
import com.proiect.library.model.BookCopy;
import com.proiect.library.model.BookStatus;
import com.proiect.library.model.Loan;
import com.proiect.library.model.Reader;
import com.proiect.library.repository.BookCopyRepository;
import com.proiect.library.repository.LoanRepository;
import com.proiect.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookCopyRepository bookCopyRepository;
    private final ReaderRepository readerRepository;

    public List<LoanDTO> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<LoanDTO> getAllLoans(Pageable pageable) {
        return loanRepository.findAll(pageable).map(this::convertToDTO);
    }

    public LoanDTO getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));
        return convertToDTO(loan);
    }

    public LoanDTO createLoan(LoanDTO loanDTO) {
        Reader reader = readerRepository.findById(loanDTO.getReaderId())
                .orElseThrow(() -> new ResourceNotFoundException("Reader not found with id: " + loanDTO.getReaderId()));

        BookCopy bookCopy = bookCopyRepository.findById(loanDTO.getBookCopyId())
                .orElseThrow(() -> new ResourceNotFoundException("Book copy not found with id: " + loanDTO.getBookCopyId()));

        if (bookCopy.getStatus() != BookStatus.AVAILABLE) {
            throw new BusinessException("Book copy is not available for loan. Current status: " + bookCopy.getStatus());
        }

        if (loanRepository.existsByBookCopy_IdAndReturnDateIsNull(loanDTO.getBookCopyId())) {
            throw new BusinessException("Book copy already has an active loan");
        }

        if (loanDTO.getDueDate().isBefore(loanDTO.getBorrowDate())) {
            throw new BusinessException("Due date cannot be before borrow date");
        }

        Loan loan = new Loan();
        loan.setReader(reader);
        loan.setBookCopy(bookCopy);
        loan.setBorrowDate(loanDTO.getBorrowDate());
        loan.setDueDate(loanDTO.getDueDate());

        bookCopy.setStatus(BookStatus.BORROWED);
        bookCopyRepository.save(bookCopy);

        Loan savedLoan = loanRepository.save(loan);
        return convertToDTO(savedLoan);
    }

    public LoanDTO updateLoan(Long id, LoanDTO loanDTO) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));

        if (loanDTO.getBorrowDate() != null) {
            loan.setBorrowDate(loanDTO.getBorrowDate());
        }
        if (loanDTO.getDueDate() != null) {
            loan.setDueDate(loanDTO.getDueDate());
        }
        if (loanDTO.getReturnDate() != null) {
            loan.setReturnDate(loanDTO.getReturnDate());
            BookCopy bookCopy = loan.getBookCopy();
            bookCopy.setStatus(BookStatus.AVAILABLE);
            bookCopyRepository.save(bookCopy);
        }

        Loan updatedLoan = loanRepository.save(loan);
        return convertToDTO(updatedLoan);
    }

    public LoanDTO returnBook(Long loanId, LocalDate returnDate) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));

        if (loan.getReturnDate() != null) {
            throw new BusinessException("Book has already been returned");
        }

        if (returnDate.isBefore(loan.getBorrowDate())) {
            throw new BusinessException("Return date cannot be before borrow date");
        }

        loan.setReturnDate(returnDate);

        BookCopy bookCopy = loan.getBookCopy();
        bookCopy.setStatus(BookStatus.AVAILABLE);
        bookCopyRepository.save(bookCopy);

        Loan updatedLoan = loanRepository.save(loan);
        return convertToDTO(updatedLoan);
    }

    public void deleteLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));

        if (loan.getReturnDate() == null) {
            // if it was loan, make book copy available again
            BookCopy bookCopy = loan.getBookCopy();
            bookCopy.setStatus(BookStatus.AVAILABLE);
            bookCopyRepository.save(bookCopy);
        }

        loanRepository.deleteById(id);
    }

    public List<LoanDTO> getLoansByReader(Long readerId) {
        if (!readerRepository.existsById(readerId)) {
            throw new ResourceNotFoundException("Reader not found with id: " + readerId);
        }
        return loanRepository.findByReader_Id(readerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LoanDTO> getOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private LoanDTO convertToDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setBookCopyId(loan.getBookCopy().getId());
        dto.setReaderId(loan.getReader().getId());
        dto.setBorrowDate(loan.getBorrowDate());
        dto.setDueDate(loan.getDueDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setReaderName(loan.getReader().getName());
        dto.setBookTitle(loan.getBookCopy().getBook().getTitle());

        // if loan is overdue
        if (loan.getReturnDate() == null && loan.getDueDate().isBefore(LocalDate.now())) {
            dto.setIsOverdue(true);
        } else {
            dto.setIsOverdue(false);
        }

        return dto;
    }
}
