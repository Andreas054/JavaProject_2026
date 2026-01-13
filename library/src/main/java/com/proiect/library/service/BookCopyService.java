package com.proiect.library.service;

import com.proiect.library.dto.BookCopyDTO;
import com.proiect.library.exception.BusinessException;
import com.proiect.library.exception.ResourceNotFoundException;
import com.proiect.library.model.Book;
import com.proiect.library.model.BookCopy;
import com.proiect.library.model.BookStatus;
import com.proiect.library.repository.BookCopyRepository;
import com.proiect.library.repository.BookRepository;
import com.proiect.library.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookCopyService {
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public List<BookCopyDTO> getAllBookCopies() {
        return bookCopyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookCopyDTO getBookCopyById(Long id) {
        BookCopy bookCopy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book copy not found with id: " + id));
        return convertToDTO(bookCopy);
    }

    public BookCopyDTO createBookCopy(BookCopyDTO bookCopyDTO) {
        Book book = bookRepository.findById(bookCopyDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookCopyDTO.getBookId()));

        BookCopy bookCopy = new BookCopy();
        bookCopy.setBook(book);
        if (bookCopyDTO.getStatus() != null) {
            bookCopy.setStatus(bookCopyDTO.getStatus());
        } else {
            bookCopy.setStatus(BookStatus.AVAILABLE);
        }

        BookCopy savedCopy = bookCopyRepository.save(bookCopy);
        return convertToDTO(savedCopy);
    }

    public BookCopyDTO updateBookCopyStatus(Long id, BookStatus status) {
        BookCopy bookCopy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book copy not found with id: " + id));

        // NO status change if copy is already borrowed
        if (bookCopy.getStatus() == BookStatus.BORROWED && status != BookStatus.BORROWED && loanRepository.existsByBookCopy_IdAndReturnDateIsNull(id)) {
            throw new BusinessException("Cannot change status of a borrowed book copy with an active loan");
        }

        bookCopy.setStatus(status);
        BookCopy updatedCopy = bookCopyRepository.save(bookCopy);
        return convertToDTO(updatedCopy);
    }

    public void deleteBookCopy(Long id) {
        if (!bookCopyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book copy not found with id: " + id);
        }

        // check if copy borrowed
        if (loanRepository.existsByBookCopy_IdAndReturnDateIsNull(id)) {
            throw new BusinessException("Cannot delete a book copy with an active loan");
        }

        bookCopyRepository.deleteById(id);
    }

    public List<BookCopyDTO> getBookCopiesByBookId(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found with id: " + bookId);
        }
        return bookCopyRepository.findByBook_Id(bookId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BookCopyDTO convertToDTO(BookCopy bookCopy) {
        BookCopyDTO dto = new BookCopyDTO();
        dto.setId(bookCopy.getId());
        dto.setBookId(bookCopy.getBook().getId());
        dto.setStatus(bookCopy.getStatus());
        dto.setBookTitle(bookCopy.getBook().getTitle());
        return dto;
    }
}

