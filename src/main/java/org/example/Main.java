package org.example;


import org.example.config.AppConfig;
import org.example.ui.UInterface;
import org.example.ui.cli.LibraryCLI;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        UInterface ui = context.getBean(LibraryCLI.class);
        ui.run();

        context.close();
    }
}