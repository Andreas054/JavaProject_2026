package com.proiect.library.controller;

import com.proiect.library.dto.AuthorDTO;
import com.proiect.library.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    @Operation(summary = "Get all authors", description = "Returns a paginated list of all authors in the database")
    public ResponseEntity<Page<AuthorDTO>> getAllAuthors(Pageable pageable) {
        return ResponseEntity.ok(authorService.getAllAuthors(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by id", description = "Returns a single author by their id")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new author", description = "Creates a new author in the database")
    public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO authorDTO) {
        AuthorDTO created = authorService.createAuthor(authorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing author", description = "Updates name for an existing author")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDTO authorDTO) {
        return ResponseEntity.ok(authorService.updateAuthor(id, authorDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an author", description = "Deletes an author from the database")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
