package com.proiect.library.service;

import com.proiect.library.dto.AuthorDTO;
import com.proiect.library.exception.DuplicateResourceException;
import com.proiect.library.exception.ResourceNotFoundException;
import com.proiect.library.model.Author;
import com.proiect.library.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorService {
    private final AuthorRepository authorRepository;

    public Page<AuthorDTO> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public AuthorDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        return convertToDTO(author);
    }

    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        // check duplicate
        if (authorRepository.findByName(authorDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("Author with name " + authorDTO.getName() + " already exists");
        }

        Author author = new Author();
        author.setName(authorDTO.getName());

        Author savedAuthor = authorRepository.save(author);
        return convertToDTO(savedAuthor);
    }

    public AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));

        // check duplicate (excluding current author)
        authorRepository.findByName(authorDTO.getName()).ifPresent(existingAuthor -> {
            if (!existingAuthor.getId().equals(id)) {
                throw new DuplicateResourceException("Author with name " + authorDTO.getName() + " already exists");
            }
        });

        author.setName(authorDTO.getName());
        Author updatedAuthor = authorRepository.save(author);
        return convertToDTO(updatedAuthor);
    }

    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }

    private AuthorDTO convertToDTO(Author author) {
        return new AuthorDTO(author.getId(), author.getName());
    }
}
