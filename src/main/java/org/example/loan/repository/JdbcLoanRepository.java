package org.example.loan.repository;

import org.example.loan.Loan;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcLoanRepository implements LoanRepository {
    private final DataSource dataSource;

    public JdbcLoanRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void save(Loan loan) {

    }

    @Override
    public Optional<Loan> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Loan> findByMemberId(int memberId) {
        return List.of();
    }

    @Override
    public List<Loan> findByBookId(int loanId) {
        return List.of();
    }

    @Override
    public Optional<Loan> findActiveByMemberAndBook(int memberId, int bookId) {
        return Optional.empty();
    }

    @Override
    public List<Loan> findAll() {
        return List.of();
    }

    @Override
    public List<Loan> findOverdue() {
        return List.of();
    }

    @Override
    public List<Loan> findActive() {
        return List.of();
    }

    @Override
    public void update(Loan loan) {

    }
}
