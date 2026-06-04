package org.example.config;

import org.example.book.repository.BookRepository;
import org.example.book.repository.JdbcBookRepository;
import org.example.loan.repository.JdbcLoanRepository;
import org.example.loan.repository.LoanRepository;
import org.example.member.repository.JdbcMemberRepository;
import org.example.member.repository.MemberRepository;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("org.example")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${db.url}")
    private String url;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;


    @Bean
    public DataSource dataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(url);
        ds.setUser(username);
        ds.setPassword(password);
        return ds;
    }
}
