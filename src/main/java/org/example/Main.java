package org.example;


import org.example.config.AppConfig;
import org.example.ui.UInterface;
import org.example.ui.cli.LibraryCLI;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        UInterface ui = context.getBean(LibraryCLI.class);
        ui.run();

        context.close();
    }
}