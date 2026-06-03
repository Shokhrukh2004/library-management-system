package org.example.loan.repository;

import org.example.loan.Loan;
import org.example.loan.dto.LoanResponse;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    void save(Loan loan);
    Optional<Loan> findById(int id);
    List<Loan> findByMemberId(int memberId);
    List<Loan> findByBookId(int bookId);
    Optional<Loan> findActiveByMemberAndBook(int memberId, int bookId);
    List<Loan> findAll();
    List<Loan> findOverdue();
    List<Loan> findActive();
    List<Loan> findReturned();
    void returnLoan(int loanId);
}
