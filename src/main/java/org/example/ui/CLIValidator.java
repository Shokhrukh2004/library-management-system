package org.example.ui;

import org.example.validation.Validator;

public class CLIValidator {

    public static void validateMenuInput(String input, int menuLength){
        Validator.validateIntRange(CLIUtil.strToInt(input), menuLength, "choice");
    }
}
