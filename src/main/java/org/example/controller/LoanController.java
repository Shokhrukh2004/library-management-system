package org.example.controller;

import jakarta.validation.Valid;
import org.example.loan.LoanService;
import org.example.loan.dto.LoanCreateRequest;
import org.example.loan.dto.LoanResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody LoanCreateRequest request){
        loanService.save(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> findAll(){
        return ResponseEntity.ok(loanService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> findById(@PathVariable int id){
        return ResponseEntity.ok(loanService.findById(id));
    }

    @GetMapping("/memberId/{id}")
    public ResponseEntity<List<LoanResponse>> findByMemberId(@PathVariable int id){
        return ResponseEntity.ok(loanService.findByMemberId(id));
    }

    @GetMapping("/bookId/{id}")
    public ResponseEntity<List<LoanResponse>> findByBookId(@PathVariable int id){
        return ResponseEntity.ok(loanService.findByBookId(id));
    }

    @GetMapping("/memberIdAndBookId/")
    public ResponseEntity<LoanResponse> findByMemberIdAndBookId(@PathVariable int memberId, @PathVariable int bookId){
        return ResponseEntity.ok(loanService.findActiveByMemberAndBook(memberId, bookId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<LoanResponse>> findAllOverdue(){
        return ResponseEntity.ok(loanService.findOverdue());
    }

    @GetMapping("/active")
    public ResponseEntity<List<LoanResponse>> findAllActive(){
        return ResponseEntity.ok(loanService.findActive());
    }

    @GetMapping("/returned")
    public ResponseEntity<List<LoanResponse>> findAllReturned(){
        return ResponseEntity.ok(loanService.findReturned());
    }

    @PatchMapping("/return/{id}")
    public ResponseEntity<Void> returnBook(@PathVariable int id){
        loanService.returnBook(id);
        return ResponseEntity.ok().build();
    }
}
