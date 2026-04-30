package org.example.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookCreateRequest {

    private String title;
    private String author;
    private String isbn;
    private int totalCopies;

}
