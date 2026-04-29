package org.example.ui;

import lombok.AllArgsConstructor;
import org.example.dto.BookCreateRequest;
import org.example.dto.BookResponse;
import org.example.dto.BookUpdateRequest;
import org.example.service.BookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class CLInterface {

    private final BookService service;
    private final Scanner scan;
    private final String appName;


    public CLInterface(BookService service, @Value("${app.name}") String appName) {
        this.service = service;
        this.scan = new Scanner(System.in);
        this.appName = appName;
    }


    public void start(){
        System.out.println("Welcome to "+appName);
        boolean isRunning = true;
        int choice;
        while (isRunning){
            printMenu();
            try{
                choice = Integer.parseInt(getInput("choice").trim());
                if(choice == 8){
                    isRunning = false;
                }
                checkInput(choice);
            }catch(NumberFormatException e){
                System.out.println("Please enter valid number!");
            }
        }
    }

    private void printMenu(){
        System.out.println("1.Add Book");
        System.out.println("2.Find by Id");
        System.out.println("3.Find by Title");
        System.out.println("4.Find by Author");
        System.out.println("5.List All");
        System.out.println("6.Update Book");
        System.out.println("7.Delete Book");
        System.out.println("8.Exit");
    }

    private String getInput(String str){
        System.out.println("Please enter the " + str + ": ");
        return scan.nextLine();
    }

    private void checkInput(int num){
        switch (num){
            case 1: {
                addBook(
                        getInput("title"),
                        getInput("author"),
                        getInput("isbn"),
                        getInput("total copies")
                );
                System.out.println("Added successfully!");
            }
            break;

            case 2: findById(getInput("book id"));
            break;

            case 3: findByTitle(getInput("title"));
            break;

            case 4: findByAuthor(getInput("author"));
            break;

            case 5: listAllBooks();
            break;

            case 6: {
                updateBook(
                        getInput("id"),
                        getInput("total copies"),
                        getInput("available copies")
                );
            }
            break;

            case 7: deleteBook(getInput("id"));
            break;
            default:
                System.out.println("Invalid input!");
        }
    }


    private void addBook(String title, String author, String isbn, String totalCopies){
        try{
            BookCreateRequest request = new BookCreateRequest(
                    title.trim(),
                    author.trim(),
                    isbn.trim(),
                    Integer.parseInt(totalCopies.trim())
            );

            service.save(request);
        }catch (IllegalArgumentException e){
            if(e instanceof NumberFormatException){
                System.out.println("Please enter an integer: " + e.getMessage());
            }
            else {
                System.out.println("Error" + e.getMessage());
            }
        }
    }


    private void findById(String id){
        try{
            int resultId = Integer.parseInt(id);
            BookResponse response = service.findById(resultId);
            System.out.println(response.toString());
        }catch (IllegalArgumentException | NoSuchElementException e){
            if(e instanceof NumberFormatException){
                System.out.println("Id should be an integer");
            }
            else{
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void findByTitle(String title){
        try{
            for(BookResponse response : service.findByTitle(title.trim())){
                System.out.println(response.toString());
            }
        }catch (NoSuchElementException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void findByAuthor(String author){
        try{
            for(BookResponse response : service.findByAuthor(author.trim())){
                System.out.println(response.toString());
            }
        }catch (NoSuchElementException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listAllBooks(){
        try{
            for(BookResponse response : service.findAll()){
                System.out.println(response.toString());
            }
        } catch (NoSuchElementException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateBook(String id, String totalCopies, String availableCopies){
        try{
            int resultId = Integer.parseInt(id.trim());
            int resultTotalCopies = Integer.parseInt(totalCopies.trim());
            int resultAvailableCopies = Integer.parseInt(availableCopies.trim());
            service.update(new BookUpdateRequest(
                    resultId,
                    resultTotalCopies,
                    resultAvailableCopies
            ));
        }catch (IllegalArgumentException | NoSuchElementException e){
            if(e instanceof NumberFormatException){
                System.out.println("Please enter an integer: " + e.getMessage());
            }
            else {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void deleteBook(String id){
        try{
            service.delete(Integer.parseInt(id));
        }catch (IllegalArgumentException | NoSuchElementException e){
            if(e instanceof NumberFormatException){
                System.out.println("Please enter integer: " + e.getMessage());
            }
            else{
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
