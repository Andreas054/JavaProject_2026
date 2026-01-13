package com.proiect.library.controller;

import com.proiect.library.dto.BookCopyDTO;
import com.proiect.library.model.BookStatus;
import com.proiect.library.service.BookCopyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-copies")
@RequiredArgsConstructor
public class BookCopyController {
    private final BookCopyService bookCopyService;

    @GetMapping
    @Operation(summary = "Get all book copies", description = "Returns a list of all book copies in the database")
    public ResponseEntity<List<BookCopyDTO>> getAllBookCopies() {
        return ResponseEntity.ok(bookCopyService.getAllBookCopies());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book copy by id", description = "Returns a single book copy by its id")
    public ResponseEntity<BookCopyDTO> getBookCopyById(@PathVariable Long id) {
        return ResponseEntity.ok(bookCopyService.getBookCopyById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new book copy", description = "Creates a new book copy in the database")
    public ResponseEntity<BookCopyDTO> createBookCopy(@Valid @RequestBody BookCopyDTO bookCopyDTO) {
        BookCopyDTO created = bookCopyService.createBookCopy(bookCopyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update book copy status", description = "Updates the Status of book copy")
    public ResponseEntity<BookCopyDTO> updateBookCopyStatus(@PathVariable Long id, @RequestParam BookStatus status) {
        return ResponseEntity.ok(bookCopyService.updateBookCopyStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book copy", description = "Deletes a book copy from the database")
    public ResponseEntity<Void> deleteBookCopy(@PathVariable Long id) {
        bookCopyService.deleteBookCopy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-book/{bookId}")
    @Operation(summary = "Get book copies by book id", description = "Returns a list of book copies for a given book id")
    public ResponseEntity<List<BookCopyDTO>> getBookCopiesByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookCopyService.getBookCopiesByBookId(bookId));
    }
}
