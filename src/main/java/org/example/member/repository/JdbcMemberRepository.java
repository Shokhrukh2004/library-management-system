package org.example.member.repository;

import org.example.member.Member;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepository implements MemberRepository {
    private final DataSource dataSource;

    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Member member) {

    }

    @Override
    public Optional<Member> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Member> findByName(String name) {
        return List.of();
    }

    @Override
    public List<Member> findAll() {
        return List.of();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void update(Member member) {

    }

    @Override
    public void deactivate(Member member) {

    }

    @Override
    public void activate(Member member) {

    }

    @Override
    public List<Member> findInactiveMembers() {
        return List.of();
    }
}
