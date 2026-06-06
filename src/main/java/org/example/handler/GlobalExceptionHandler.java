package org.example.handler;

import org.example.exception.*;
import org.springframework.stereotype.Component;

@Component
public class GlobalExceptionHandler extends Throwable {

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
        else if(e instanceof DatabaseException){
            System.out.println("Database Error: " + e.getMessage());
        }
        else if(e instanceof LibraryException){
            System.out.println("Library Error: " + e.getMessage());
        }
        else{
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }
}
