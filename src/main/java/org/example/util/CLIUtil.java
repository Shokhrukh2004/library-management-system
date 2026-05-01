package org.example.util;

import org.example.exception.ValidationException;

import java.util.List;
import java.util.Scanner;

public class CLIUtil {

    private static Scanner scan = new Scanner(System.in);

    public static int strToInt(String str){
        try{
            return Integer.parseInt(str.trim());
        }catch (NumberFormatException e){
            throw new ValidationException(str + " is not a number, please enter a valid number.");
        }
    }

    public static void listObjects(List<?> items){
        items.forEach(item -> System.out.println(item.toString()));
    }

    public static String getInput(String prompt){
        System.out.println("Enter the " + prompt + ":");
        return scan.nextLine().trim();
    }

}
