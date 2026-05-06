package org.example.ui.cli;

import org.example.exception.LibraryException;
import org.example.handler.GlobalExceptionHandler;
import org.example.ui.UInterface;
import org.example.util.CLIUtil.*;
import org.example.validation.CLIValidator;
import org.springframework.stereotype.Component;

import static org.example.util.CLIUtil.*;


@Component
public class LibraryCLI implements UInterface {
    private final BookCLI bookCLI;
    private final MemberCLI memberCLI;
    private final LoanCLI loanCLI;
    private final GlobalExceptionHandler handler;

    public LibraryCLI(BookCLI bookCLI,
                      GlobalExceptionHandler handler,
                      MemberCLI memberCLI,
                      LoanCLI loanCLI) {
        this.bookCLI = bookCLI;
        this.handler = handler;
        this.memberCLI = memberCLI;
        this.loanCLI = loanCLI;
    }

    public void run(){
        boolean isRunning = true;
        while (isRunning){
            try{
                printMenu();
                int choice = getInputInt("choice");
                CLIValidator.validateMenuInput(choice, 4);
                if(choice == 4){
                    isRunning = false;
                }else {
                    checkInput(choice);
                }
            }catch (LibraryException e){
                handler.handle(e);
            }
        }
    }

    private void printMenu(){
        System.out.println("=== Welcome to Library Main Page ===");
        System.out.println("1. Go to Book Page");
        System.out.println("2. Go to Member Page");
        System.out.println("3. Go to Loan Page");
        System.out.println("4. Exit");
    }

    private void checkInput(int choice){
        switch (choice){
            case 1: bookCLI.run();break;
            case 2: memberCLI.run();break;
            case 3: loanCLI.run();break;
            default: System.out.println("Please enter a valid choice: this is not valid choice. " + choice);
        }
    }
}
