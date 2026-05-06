package org.example.loan.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.loan.Loan;
import org.example.loan.enums.Status;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonLoanRepository implements LoanRepository{
    private final String filePath;
    private final ObjectMapper mapper;
    private int availableId;
    private final List<Loan> loans;

    public JsonLoanRepository(ObjectMapper mapper, String filePath) {
        this.mapper = mapper;
        this.filePath = filePath;
        this.loans = new ArrayList<>();
        this.availableId = 0;
    }

    @Override
    public void save(Loan loan) {
        loan.setId(getAvailableId());
        loans.add(loan);
    }

    @Override
    public Optional<Loan> findById(int id) {
        return loans.stream()
                .filter(loan -> loan.getId() == id)
                .findFirst();
    }

    @Override
    public List<Loan> findByMemberId(int memberId) {
        return loans.stream()
                .filter(loan -> loan.getMemberId() == memberId)
                .toList();
    }

    @Override
    public List<Loan> findByBookId(int bookId) {
        return loans.stream()
                .filter(loan -> loan.getBookId() == bookId)
                .toList();
    }

    @Override
    public Optional<Loan> findActiveByMemberAndBook(int memberId, int bookId) {
        return loans.stream()
                .filter(loan ->
                        loan.getBookId() == bookId &&
                                loan.getMemberId() == memberId)
                .findFirst();
    }

    @Override
    public List<Loan> findAll() {
        return new ArrayList<>(loans);
    }

    @Override
    public List<Loan> findOverdue() {
        return loans.stream()
                .filter(loan -> loan.getStatus()
                        .equals(Status.OVERDUE))
                .toList();
    }

    @Override
    public List<Loan> findActive() {
        return loans.stream()
                .filter(loan -> loan.getStatus()
                        .equals(Status.ACTIVE))
                .toList();
    }

    @Override
    public void update(Loan updatedLoan) {
        Loan loan = findById(updatedLoan.getId()).get();
        loan.setStatus(updatedLoan.getStatus());
        loan.setReturnDate(updatedLoan.getReturnDate());
    }

    @PostConstruct
    public void load(){
        try{
            File file = new File(filePath);
            if(!file.exists()){
                return;
            }

            List<Loan> loaded = mapper.readValue(
                    file,
                    new TypeReference<List<Loan>>() {
            });
            loans.addAll(loaded);

            availableId = loaded.stream()
                    .mapToInt(Loan::getId)
                    .max()
                    .orElse(0);

            System.out.println("Loan has been loaded.");

        }catch (IOException e){
            System.out.println("Loans file could not be loaded: " + e.getMessage());
        }
    }

    @PreDestroy
    public void saveFile(){
        try {
            File file = new File(filePath);
            mapper.writeValue(file, loans);
            System.out.println("Loan file has been saved.");
        }catch (IOException e){
            System.out.println("Loans file could not be saved: " + e.getMessage());
        }
    }

    private int getAvailableId() {
        return ++availableId;
    }
}
