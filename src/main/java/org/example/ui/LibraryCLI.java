package org.example.ui;

import org.springframework.stereotype.Component;

import org.example.ui.CLIUtil;

@Component
public class LibraryCLI {
    private final BookCLI bookCLI;

    public LibraryCLI(BookCLI bookCLI) {
        this.bookCLI = bookCLI;
    }

    public void run(){
        printMenu();
        checkInput(CLIUtil.strToInt(CLIUtil.getInput("choice")));
    }

    private void printMenu(){
        System.out.println("=== Welcome to Library Main Page ===");
        System.out.println("1. Go to Book Page");
        System.out.println("2. Go to Member Page");
    }

    private void checkInput(int choice){
        switch (choice){
            case 1: bookCLI.run();break;
            default: System.out.println("Invalid choice");
        }
    }



}
