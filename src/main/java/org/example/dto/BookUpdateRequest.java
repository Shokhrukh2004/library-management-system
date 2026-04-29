package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class BookUpdateRequest {

    private final int id;
    private final int totalCopies;
    private final int availableCopies;
}
