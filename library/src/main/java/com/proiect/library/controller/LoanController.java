package com.proiect.library.controller;

import com.proiect.library.dto.LoanDTO;
import com.proiect.library.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @GetMapping
    @Operation(summary = "Get all loans", description = "Returns a list of all loans in the database")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan by id", description = "Returns a single loan by its id")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new loan", description = "Creates a new loan in the database")
    public ResponseEntity<LoanDTO> createLoan(@Valid @RequestBody LoanDTO loanDTO) {
        LoanDTO created = loanService.createLoan(loanDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/return")
    @Operation(summary = "Return a book", description = "Marks book as returned")
    public ResponseEntity<LoanDTO> returnBook(@PathVariable Long id, @RequestParam(required = false) LocalDate returnDate) {
        LocalDate actualReturnDate = returnDate != null ? returnDate : LocalDate.now();
        return ResponseEntity.ok(loanService.returnBook(id, actualReturnDate));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a loan", description = "Deletes a loan from the database")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-reader/{readerId}")
    @Operation(summary = "Get loans by reader id", description = "Returns a list of loans for a given reader id")
    public ResponseEntity<List<LoanDTO>> getLoansByReader(@PathVariable Long readerId) {
        return ResponseEntity.ok(loanService.getLoansByReader(readerId));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue loans", description = "Returns a list of all overdue loans")
    public ResponseEntity<List<LoanDTO>> getOverdueLoans() {
        return ResponseEntity.ok(loanService.getOverdueLoans());
    }
}
