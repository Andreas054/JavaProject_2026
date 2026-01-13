package com.proiect.library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReaderDTO {
    private Long id;

    @NotBlank(message = "Name required")
    private String name;

    @NotBlank(message = "Email required")
    @Email(message = "Email must be valid")
    private String email;

    private Integer activeLoansCount;
}

