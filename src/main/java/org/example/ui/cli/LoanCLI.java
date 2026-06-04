package org.example.ui.cli;

import org.example.exception.LibraryException;
import org.example.handler.GlobalExceptionHandler;
import org.example.loan.Loan;
import org.example.loan.LoanService;
import org.example.loan.dto.LoanCreateRequest;
import org.example.loan.dto.LoanResponse;
import org.example.util.CLIUtil;
import org.example.validation.CLIValidator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LoanCLI {
    private final LoanService service;
    private final GlobalExceptionHandler handler;

    public LoanCLI(LoanService service, GlobalExceptionHandler handler) {
        this.service = service;
        this.handler = handler;
    }

    public void run(){
        boolean isRunning = true;

        while(isRunning){
            try{
                printMenu();
                int input = CLIUtil.getInputInt("choice");
                CLIValidator.validateMenuInput(input, 11);

                if(input==11){
                    isRunning = false;
                }else {
                    checkInput(input);
                }
            }catch(LibraryException e){
                handler.handle(e);
            }
        }
    }

    private void checkInput(int choice){
        switch (choice){
            case 1: takeLoan(); break;
            case 2: findById(); break;
            case 3: findByMemberId(); break;
            case 4: findByBookId(); break;
            case 5: findActiveByMemberAndBook(); break;
            case 6: findAll(); break;
            case 7: findAllOverdue(); break;
            case 8: findAllActive(); break;
            case 9: findAllReturned(); break;
            case 10: returnLoan(); break;
        }
    }

    private void printMenu(){
        System.out.println("=== Welcome to Loan Page ===");
        System.out.println("1. Take a loan");
        System.out.println("2. Find loan by id");
        System.out.println("3. Find loan by member id");
        System.out.println("4. Find loan by book id");
        System.out.println("5. Find active loan by member and book ids");
        System.out.println("6. Find all the loans");
        System.out.println("7. Find all loans past return dates");
        System.out.println("8. Find all the loans still active");
        System.out.println("9. Find all the loans returned");
        System.out.println("10. Return a loan");
        System.out.println("11. Back");
    }

    private void takeLoan(){
        service.save(new LoanCreateRequest(
                CLIUtil.getInputInt("Member id"),
                CLIUtil.getInputInt("Book id")
        ));
    }

    private void findById(){
        LoanResponse loan = service.findById(CLIUtil.getInputInt("Loan id"));
        System.out.println(loan.toString());
    }

    private void findByMemberId(){
        List<LoanResponse> loans = service.findByMemberId(CLIUtil.getInputInt("Member id"));
        loans.forEach(loan -> System.out.println(loan.toString()));
    }

    private void findByBookId(){
        List<LoanResponse> loans = service.findByBookId(CLIUtil.getInputInt("Book id"));
        loans.forEach(loan -> System.out.println(loan.toString()));
    }

    private void findActiveByMemberAndBook(){
        int memberId = CLIUtil.getInputInt("Member id");
        int bookId = CLIUtil.getInputInt("Book id");
        LoanResponse loan = service.findActiveByMemberAndBook(memberId, bookId);
        System.out.println(loan.toString());
    }

    private void findAll(){
        service.findAll()
                .forEach(loan -> System.out.println(loan.toString()));
    }

    private void findAllOverdue(){
        service.findOverdue()
                .forEach(loan -> System.out.println(loan.toString()));
    }

    private void findAllActive(){
        service.findActive()
                .forEach(loan -> System.out.println(loan.toString()));
    }

    private void findAllReturned(){
        service.findReturned()
                .forEach(loan -> System.out.println(loan.toString()));
    }

    private void returnLoan(){
        service.returnBook(CLIUtil.getInputInt("Loan id"));
    }

}



