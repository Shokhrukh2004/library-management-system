package org.example.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Book {

    @Setter
    private int id;
    private String title;
    private String author;
    private String isbn;
    @Setter
    private int totalCopies;
    @Setter
    private int availableCopies;

}
