package org.example.book.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookCreateRequest {

    @NotBlank(message = "Title is required")
    @Min(value = 3, message = "Minimum 3 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Min(value = 3, message = "Minimum 3 characters")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Min(value = 3, message = "Minimum 3 characters")
    private String isbn;

    @Positive(message = "Total copies must be positive")
    private int totalCopies;
}
