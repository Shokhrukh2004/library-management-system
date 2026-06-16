package org.example.member;

import org.example.exception.ConflictException;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.member.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MemberConflictLogic {

    private final Logger log = LoggerFactory.getLogger(MemberConflictLogic.class);
    private final MemberRepository memberRepo;
    private final LoanRepository loanRepo;

    public MemberConflictLogic(MemberRepository memberRepo, LoanRepository loanRepo) {
        this.memberRepo = memberRepo;
        this.loanRepo = loanRepo;
    }

    public void emailDuplicateCheck(String email){
        if(memberRepo.findByEmail(email).isPresent()){
            log.warn("Email already exists - email: {}", email);
            throw new ConflictException("Email already exists");
        }
    }

    public void isMemberLoanedCheck(int id){
        boolean isActive = loanRepo.existsByMember_IdAndStatus(id, Status.ACTIVE);

        if(isActive){
            log.warn("Member has active loan - memberId: {}", id);
            throw new ConflictException("The member with id "+ id + " has active loan");
        }
    }

    public void isMemberActiveCheck(Member member){
        if(!member.isActive()){
            log.warn("Member is not active already - memberId: {}", member.getId());
            throw new ConflictException("Member is already inactive");
        }
    }

    public void isMemberNotActiveCheck(Member member){
        if(member.isActive()){
            log.warn("Member is active already - memberId: {}", member.getId());
            throw new ConflictException("Member is already activated");
        }
    }
}
