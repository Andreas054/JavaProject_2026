package com.proiect.library.repository;

import com.proiect.library.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByReader_Id(Long readerId);

    List<Loan> findByReader_IdAndReturnDateIsNull(Long readerId);

    @Query("SELECT l FROM Loan l WHERE l.returnDate IS NULL AND l.dueDate < :date")
    List<Loan> findOverdueLoans(LocalDate date);

    boolean existsByBookCopy_IdAndReturnDateIsNull(Long bookCopyId);
}

