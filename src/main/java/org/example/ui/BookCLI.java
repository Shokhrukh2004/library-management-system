package org.example.ui;

import org.example.book.BookService;
import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookResponse;
import org.example.book.dto.BookUpdateRequest;
import org.example.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import org.example.ui.CLIUtil;

import static org.example.ui.CLIUtil.getInput;
import static org.example.ui.CLIUtil.strToInt;


@Component
public class BookCLI implements CLI {

    private final BookService service;

    public BookCLI(BookService service) {
        this.service = service;
    }

    @Override
    public void run(){
        try{
            while(true){
                printMenu();
                String inputStr = getInput("choice");
                CLIValidator.validateMenuInput(inputStr, 8);
                checkInput(strToInt(inputStr));
            }

        }catch (Exception e){
            System.out.println("Invalid input");
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
        System.out.println("9. Exit");
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

            default: System.out.println("Invalid choice");
        }
    }

    private void addBook() {
        try{

            service.save(
                    new BookCreateRequest(
                            getInput( "title"),
                            getInput("author"),
                            getInput("ISBN"),
                            strToInt(getInput("total copies"))
                    )
            );
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, please enter integer for total copies.");
        }
    }

    private void updateBook(){
        try{
            service.update(
                    new BookUpdateRequest(
                            strToInt(getInput("Id")),
                            strToInt(getInput("total copies")),
                            strToInt(getInput("available copies"))
                    )
            );
        }catch (NumberFormatException e) {
            System.out.println("Invalid input, please enter valid integer for update.");
        }
    }

    private void findById(){
        try{
            int id = strToInt(getInput("Id"));
            System.out.println(service.findById(id).toString());

        }catch (NumberFormatException e){
            System.out.println("Invalid input, please enter valid integer for id.");
        }
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
        try{
            service.delete(Integer.parseInt(getInput("Id")));
        }catch (NumberFormatException e){
            System.out.println("Invalid input, please enter valid integer for id.");
        }
    }

}
