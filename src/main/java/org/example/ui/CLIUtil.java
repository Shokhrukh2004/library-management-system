package org.example.ui;

import org.example.book.Book;
import org.example.book.dto.BookResponse;

import java.util.List;
import java.util.Scanner;

public class CLIUtil {
    public static int strToInt(String str){
        return Integer.parseInt(str.trim());
    }

    public static void listObjects(List<BookResponse> books){
        if(books == null || books.isEmpty()){
            System.out.println("No books found");
            return;
        }
        for(BookResponse book : books){
            System.out.println(book.toString());
        }
    }

    public static String getInput(String prompt){
        System.out.println("Enter the " + prompt + ":");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine().trim();
    }
}
