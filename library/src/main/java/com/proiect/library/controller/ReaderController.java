package com.proiect.library.controller;

import com.proiect.library.dto.ReaderDTO;
import com.proiect.library.service.ReaderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class ReaderController {
    private final ReaderService readerService;

    @GetMapping
    @Operation(summary = "Get all readers", description = "Returns a paginated list of all readers in the database")
    public ResponseEntity<Page<ReaderDTO>> getAllReaders(Pageable pageable) {
        return ResponseEntity.ok(readerService.getAllReaders(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reader by id", description = "Returns a single reader by their id")
    public ResponseEntity<ReaderDTO> getReaderById(@PathVariable Long id) {
        return ResponseEntity.ok(readerService.getReaderById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new reader", description = "Creates a new reader in the database")
    public ResponseEntity<ReaderDTO> createReader(@Valid @RequestBody ReaderDTO readerDTO) {
        ReaderDTO created = readerService.createReader(readerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing reader", description = "Updates existing reader (Name, Email)")
    public ResponseEntity<ReaderDTO> updateReader(@PathVariable Long id, @Valid @RequestBody ReaderDTO readerDTO) {
        return ResponseEntity.ok(readerService.updateReader(id, readerDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reader", description = "Deletes a reader from the database")
    public ResponseEntity<Void> deleteReader(@PathVariable Long id) {
        readerService.deleteReader(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/with-active-loans")
    @Operation(summary = "Get readers with active loans", description = "Returns a list of readers who have active loans")
    public ResponseEntity<List<ReaderDTO>> getReadersWithActiveLoans() {
        return ResponseEntity.ok(readerService.getReadersWithActiveLoans());
    }
}
