package org.example.ui.cli;

import org.example.book.BookService;
import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookUpdateRequest;
import org.example.exception.LibraryException;
import org.example.handler.GlobalExceptionHandler;
import org.example.util.CLIUtil;
import org.example.validation.CLIValidator;
import org.springframework.stereotype.Component;

import static org.example.util.CLIUtil.getInput;
import static org.example.util.CLIUtil.*;


@Component
public class BookCLI {

    private final BookService service;
    private final GlobalExceptionHandler handler;

    public BookCLI(BookService service, GlobalExceptionHandler handler) {
        this.service = service;
        this.handler = handler;
    }

    public void run() {
        boolean isRunning = true;

        while (isRunning) {
            try {
                printMenu();
                int choice = strToInt(getInput("choice"));
                CLIValidator.validateMenuInput(choice, 8);
                isRunning = !(choice == 8);
                checkInput(choice);
            }catch(LibraryException e) {
                handler.handle(e);
            }
        }
    }

    private void printMenu(){
        System.out.println("=== Welcome to Books Page ===");
        System.out.println("1. Add book");
        System.out.println("2. Find by id");
        System.out.println("3. Find by title");
        System.out.println("4. Find by author");
        System.out.println("5. List all books");
        System.out.println("6. Update book");
        System.out.println("7. Delete by id");
        System.out.println("8. Back");
    }

    private void checkInput(int choice){
        switch (choice){
            case 1: addBook(); break;
            case 2: findById(); break;
            case 3: findByTitle(); break;
            case 4: findByAuthor(); break;
            case 5: findAll(); break;
            case 6: updateBook(); break;
            case 7: deleteById(); break;

            default: System.out.println("Please enter a valid choice: this is not valid choice. " + choice);
        }
    }

    private void addBook() {
        service.save(
                new BookCreateRequest(
                        getInput( "title"),
                        getInput("author"),
                        getInput("ISBN"),
                        strToInt(getInput("total copies"))
                )
        );

    }

    private void updateBook(){
        service.update(
                new BookUpdateRequest(
                        strToInt(getInput("Id")),
                        strToInt(getInput("total copies")),
                        strToInt(getInput("available copies"))
                )
        );
    }

    private void findById(){
        int id = strToInt(getInput("Id"));
        System.out.println(service.findById(id).toString());
    }

    private void findByTitle(){
        CLIUtil.listObjects(
                service.findByTitle(getInput("title")
                )
        );
    }

    private void findByAuthor(){
        CLIUtil.listObjects(
                service.findByAuthor(getInput("author")
                )
        );
    }

    private void findAll(){
        CLIUtil.listObjects(service.findAll());
    }

    private void deleteById(){
        service.delete(strToInt(getInput("Id")));
    }
}
