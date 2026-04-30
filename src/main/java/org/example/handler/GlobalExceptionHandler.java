package org.example.handler;

import org.example.exception.ConflictException;
import org.example.exception.LibraryException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class GlobalExceptionHandler {

    public void handle(Exception e) {
        if(e instanceof ValidationException){
            System.out.println("Validation Error: " + e.getMessage());
        }
        else if(e instanceof NotFoundException){
            System.out.println("Not Found: " + e.getMessage());
        }
        else if(e instanceof ConflictException){
            System.out.println("Conflict Error: " + e.getMessage());
        }
        else if(e instanceof LibraryException){
            System.out.println("Library Error: " + e.getMessage());
        }
        else{
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }
}
