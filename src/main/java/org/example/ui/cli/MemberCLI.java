package org.example.ui.cli;

import org.example.exception.LibraryException;
import org.example.handler.GlobalExceptionHandler;
import org.example.member.MemberService;
import org.example.member.dto.MemberCreateRequest;
import org.example.member.dto.MemberUpdateRequest;
import org.example.util.CLIUtil;
import org.example.validation.CLIValidator;
import org.springframework.stereotype.Component;

@Component
public class MemberCLI {
    private final MemberService service;
    private final GlobalExceptionHandler handler;

    public MemberCLI(MemberService service,
                     GlobalExceptionHandler handler) {
        this.service = service;
        this.handler = handler;
    }

    public void run(){
        boolean isRunning = true;

        while(isRunning){
            try{
                printMenu();
                int input = CLIUtil.getInputInt("choice");
                CLIValidator.validateMenuInput(input, 8);

                if(input==8){
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
            case 1: addMember(); break;
            case 2: findById(); break;
            case 3: findByName(); break;
            case 4: findByEmail(); break;
            case 5: findAll(); break;
            case 6: update(); break;
            case 7: delete(); break;
        }
    }

    private void printMenu(){
        System.out.println("=== Welcome to Members Page ===");
        System.out.println("1. Add Member");
        System.out.println("2. Find by id");
        System.out.println("3. Find by name");
        System.out.println("4. Find by email");
        System.out.println("5. List all members");
        System.out.println("6. Update member");
        System.out.println("7. Delete by id");
        System.out.println("8. Back");
    }

    private void addMember(){
        service.addMember(new MemberCreateRequest(
                CLIUtil.getInputStr("Name"),
                CLIUtil.getInputStr("Email")
        ));
    }

    private void findById(){
        int id = CLIUtil.getInputInt("Id");
        System.out.println(service.findById(id).toString());
    }

    private void findByName(){
        String name = CLIUtil.getInputStr("Name");
        CLIUtil.listObjects(service.findByName(name));
    }

    private void findByEmail(){
        String email = CLIUtil.getInputStr("Email");
        System.out.println(service.findByEmail(email).toString());
    }

    private void update(){
        service.update(new MemberUpdateRequest(
                CLIUtil.getInputInt("Id"),
                CLIUtil.getInputStr("Name"),
                CLIUtil.getInputStr("Email")
        ));
    }

    private void delete(){
        service.delete(CLIUtil.getInputInt("Id"));
    }

    private void findAll(){
        service.findAll()
                .forEach(member -> System.out.println(member.toString()));
    }
}
