package org.example.book;

import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookResponse;
import org.example.book.dto.BookUpdateRequest;

public class BookParser {

    public static Book toBookFromCreateRequest(BookCreateRequest request){
        return new Book(
                0,
                request.getTitle(),
                request.getAuthor(),
                request.getIsbn().trim(),
                request.getTotalCopies(),
                request.getTotalCopies()
        );
    }

    public static BookResponse toBookResponse(Book book){
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }
}
