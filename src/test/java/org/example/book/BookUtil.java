package org.example.book;

import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookUpdateRequest;

public class BookUtil {

    public static Book getBook(boolean isActive){
        return new Book(
                1,
                "Clean Code",
                "John Doe",
                "abc123",
                10,
                10,
                isActive
        );
    }

    public static BookUpdateRequest getUpdateRequest(boolean isValid){
        return new BookUpdateRequest(
                1,
                20,
                isValid ? 20 : 25
        );
    }

    public static BookCreateRequest getCreateRequest(){
        return new BookCreateRequest(
                "Clean Code",
                "John Doe",
                "abc123",
                20
        );
    }
}
