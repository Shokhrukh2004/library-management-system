package org.example.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class BookUpdateRequest {

    @Positive(message = "id must be positive")
    private final int id;

    @Positive(message = "Total copies must be positive")
    private final int totalCopies;

    @Positive(message = "Available copies must be positive")
    private final int availableCopies;
}
