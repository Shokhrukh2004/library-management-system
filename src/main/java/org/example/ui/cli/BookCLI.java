package org.example.ui.cli;

import org.example.book.BookService;
import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookUpdateRequest;
import org.example.exception.LibraryException;
import org.example.handler.GlobalExceptionHandler;
import org.example.util.CLIUtil;
import org.example.validation.CLIValidator;
import org.springframework.stereotype.Component;

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
                int choice = getInputInt("choice");
                CLIValidator.validateMenuInput(choice, 10);

                if(choice == 10) {
                    isRunning = false;
                }else {
                    checkInput(choice);
                }

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
        System.out.println("7. Deactivate by id");
        System.out.println("8. Activate by title");
        System.out.println("9. List all inactive books");
        System.out.println("10. Back");
    }

    private void checkInput(int choice){
        switch (choice){
            case 1: addBook(); break;
            case 2: findById(); break;
            case 3: findByTitle(); break;
            case 4: findByAuthor(); break;
            case 5: findAll(); break;
            case 6: updateBook(); break;
            case 7: deactivateById(); break;
            case 8: activateById(); break;
            case 9: findAllInactive(); break;
        }
    }

    private void addBook() {
        service.save(
                new BookCreateRequest(
                        getInputStr( "title"),
                        getInputStr("author"),
                        getInputStr("ISBN"),
                        getInputInt("total copies")
                )
        );

    }

    private void updateBook(){
        service.update(
                new BookUpdateRequest(
                        getInputInt("Id"),
                        getInputInt("total copies"),
                        getInputInt("available copies")
                )
        );
    }

    private void findById(){
        int id = getInputInt("Id");
        System.out.println(service.findById(id).toString());
    }

    private void findByTitle(){
        CLIUtil.listObjects(
                service.findByTitle(getInputStr("title")
                )
        );
    }

    private void findByAuthor(){
        CLIUtil.listObjects(
                service.findByAuthor(getInputStr("author")
                )
        );
    }

    private void findAll(){
        CLIUtil.listObjects(service.findAll());
    }

    private void deactivateById(){
        service.deactivate(getInputInt("Id"));
    }

    private void activateById(){
        service.activate(getInputInt("Id"));
    }

    private void findAllInactive(){
        CLIUtil.listObjects(service.findAllInactive());
    }
}
