package com.proiect.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_copy_id", nullable = false)
    @NotNull(message = "Book copy required")
    private BookCopy bookCopy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reader_id", nullable = false)
    @NotNull(message = "Reader required")
    private Reader reader;

    @NotNull(message = "Borrow date required")
    @Column(nullable = false)
    private LocalDate borrowDate;

    @NotNull(message = "Due date required")
    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;
}
