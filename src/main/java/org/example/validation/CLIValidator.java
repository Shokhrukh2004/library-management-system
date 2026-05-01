package org.example.validation;

import org.example.util.CLIUtil;

public class CLIValidator {

    public static void validateMenuInput(int choice, int menuLength){
        Validator.validateIntRange(choice, menuLength, "choice");
    }
}
