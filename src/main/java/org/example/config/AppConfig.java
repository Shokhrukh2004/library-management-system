package org.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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

    @Value("${hikari.max.pool.size}")
    private int poolSize;

    @Value("${hikari.min.idle}")
    private int minIdle;

    @Value("${hikari.connection.timeout}")
    private int connTimeout;

    @Value("${hikari.idle.timeout}")
    private int idleTimeout;

    @Value("${hikari.max.lifetime}")
    private int maxLifetime;


    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(minIdle);

        config.setConnectionTimeout(connTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);

        config.setConnectionTestQuery("SELECT 1");

        config.setPoolName("LibraryPool");

        return new HikariDataSource(config);
    }
}
