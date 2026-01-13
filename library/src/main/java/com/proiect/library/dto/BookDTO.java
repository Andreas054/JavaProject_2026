package com.proiect.library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDTO {
    private Long id;

    @NotBlank(message = "Title required")
    private String title;

    @NotBlank(message = "ISBN required")
    private String isbn;

    @NotNull(message = "Publication year required")
    @Min(value = 1000, message = "Publication year > 1000")
    private Integer publicationYear;

    @NotEmpty(message = "At least one author required")
    private Set<Long> authorIds;

    @NotEmpty(message = "At least one genre required")
    private Set<Long> genreIds;

    private Set<AuthorDTO> authors;
    private Set<GenreDTO> genres;
    private Integer availableCopies;
}

