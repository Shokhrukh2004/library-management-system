package org.example.member.repository;

import org.example.member.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    void save(Member member);

    Optional<Member> findById(int id);

    List<Member> findByName(String name);

    List<Member> findAll();

    Member findByEmail(String email);

    void update(Member member);

    void delete(int id);
}
