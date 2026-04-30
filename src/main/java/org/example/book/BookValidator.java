package org.example.book;

import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookUpdateRequest;
import org.example.validation.Validator;

public class BookValidator {

    public static void validateUpdateRequest(BookUpdateRequest request) {
        Validator.validateNotNull(request, "Update request");

        Validator.validatePositiveInt(request.getId(), "Id");
        Validator.validatePositiveInt(request.getTotalCopies(), "Total Copies");
        Validator.validatePositiveInt(request.getAvailableCopies(), "Available Copies");
    }

    public static void validateCreateRequest(BookCreateRequest request) {
        Validator.validateNotNull(request, "Create requested");
        Validator.validateString(request.getTitle(), "Title");
        Validator.validateString(request.getAuthor(), "Author");
        Validator.validateString(request.getIsbn(), "ISBN");
        Validator.validatePositiveInt(request.getTotalCopies(), "Total Copies");
    }

}
