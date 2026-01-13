package com.proiect.library.repository;

import com.proiect.library.model.BookCopy;
import com.proiect.library.model.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findByBook_Id(Long bookId);

    long countByBook_IdAndStatus(Long bookId, BookStatus status);
}

