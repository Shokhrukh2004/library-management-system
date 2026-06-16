package org.example.loan.repository;

import org.example.loan.Loan;
import org.example.loan.dto.LoanResponse;
import org.example.loan.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Integer> {
    List<Loan> findByMember_Id(int memberId);

    List<Loan> findByBook_Id(int bookId);

    Optional<Loan> findByMember_IdAndBook_IdAndStatus(int memberId, int bookId, Status status);

    List<Loan> findByStatus(Status status);

    boolean existsByBook_IdAndStatus(int bookId, Status status);

    boolean existsByMember_IdAndStatus(int memberId, Status status);
}
