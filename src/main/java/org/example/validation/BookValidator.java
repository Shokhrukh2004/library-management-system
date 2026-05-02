package org.example.validation;

import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookUpdateRequest;
import org.example.exception.ValidationException;

public class BookValidator {

    public static void validateUpdateRequest(BookUpdateRequest request) {
        Validator.validateNotNull(request, "Update request");

        Validator.validatePositiveInt(request.getId(), "Id");
        Validator.validatePositiveInt(request.getTotalCopies(), "Total Copies");
        Validator.validatePositiveInt(request.getAvailableCopies(), "Available Copies");
        validateCopies(request.getTotalCopies(), request.getAvailableCopies());
    }

    public static void validateCreateRequest(BookCreateRequest request) {
        Validator.validateNotNull(request, "Create requested");
        Validator.validateString(request.getTitle(), "Title");
        Validator.validateString(request.getAuthor(), "Author");
        Validator.validateString(request.getIsbn(), "ISBN");
        Validator.validatePositiveInt(request.getTotalCopies(), "Total Copies");
    }

    private static void validateCopies(int totalCopies, int availableCopies){
        if(totalCopies < availableCopies){
            throw new ValidationException("Total Copies can't be less than Available Copies");
        }
    }

}
