package com.proiect.library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.proiect.library.model.BookStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookCopyDTO {
    private Long id;

    @NotNull(message = "Book ID required")
    private Long bookId;

    @NotNull(message = "Status required")
    private BookStatus status;

    private String bookTitle;
}

