package org.example;


import org.example.config.AppConfig;
import org.example.ui.CLInterface;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        CLInterface ui = context.getBean(CLInterface.class);
        ui.start();

        context.close();
    }
}