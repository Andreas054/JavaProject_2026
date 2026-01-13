package com.proiect.library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanDTO {
    private Long id;

    @NotNull(message = "Book copy ID required")
    private Long bookCopyId;

    @NotNull(message = "Reader ID required")
    private Long readerId;

    @NotNull(message = "Borrow date required")
    private LocalDate borrowDate;

    @NotNull(message = "Due date required")
    private LocalDate dueDate;

    private LocalDate returnDate;

    private String readerName;
    private String bookTitle;
    private Boolean isOverdue;
}

