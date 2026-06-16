package org.example.member.repository;

import org.example.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    Optional<Member> findByEmail(String email);

    List<Member> findByNameContainingIgnoreCase(String name);

    List<Member> findByIsActive(boolean isActive);
}
