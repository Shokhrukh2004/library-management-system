package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.book.repository.BookRepository;
import org.example.book.repository.JsonBookRepository;
import org.example.loan.repository.JsonLoanRepository;
import org.example.loan.repository.LoanRepository;
import org.example.member.repository.JsonMemberRepository;
import org.example.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("org.example")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${app.books.file}")
    private String booksFilePath;

    @Value("${app.members.file}")
    private String membersFilePath;

    @Value("${app.loans.file}")
    private String loansFilePath;



    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public BookRepository bookRepository() {
        return new JsonBookRepository(objectMapper(), booksFilePath);
    }

    @Bean
    public MemberRepository memberRepository() {
        return new JsonMemberRepository(objectMapper(), membersFilePath);
    }

    @Bean
    public LoanRepository loanRepository() {
        return new JsonLoanRepository(objectMapper(), loansFilePath);
    }
}
