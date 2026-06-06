package org.example.member.repository;

import org.example.exception.DatabaseException;
import org.example.member.Member;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemberRepository implements MemberRepository {
    private final DataSource dataSource;

    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Member member) {
        String sql = "INSERT INTO members (name, email, register_date, is_active) VALUES(?, ?, ?, ?) ";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setDate(3, Date.valueOf(member.getRegisterDate()));
            ps.setBoolean(4, member.isActive());

            ps.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException("Could not save member", e);
        }
    }

    @Override
    public Optional<Member> findById(int id) {
        String sql = "SELECT * FROM members where id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return Optional.of(mapRow(rs));
            }
        }catch (SQLException e){
            throw new DatabaseException("Could not find member", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Member> findByName(String name) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE name ILIKE ? AND is_active = true";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + name + "%");

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                members.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Could not find members", e);
        }

        return members;
    }

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE is_active = true";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                members.add(mapRow(rs));
            }
        }catch (SQLException e){
            throw new DatabaseException("Could not find members", e);
        }

        return members;
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT * FROM members WHERE email ILIKE ? AND is_active = true";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + email + "%");
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return Optional.of(mapRow(rs));
            }
        }catch (SQLException e){
            throw new DatabaseException("Could not find members", e);
        }

        return Optional.empty();
    }

    @Override
    public void update(Member member) {
        String sql = "UPDATE members SET name = ?, email = ?, WHERE id = ? AND is_active = true";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setInt(3, member.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not update member", e);
        }

    }

    @Override
    public void deactivate(int id) {
        String sql = "UPDATE members SET is_active = false WHERE id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not deactivate member", e);
        }
    }

    @Override
    public void activate(int id) {
        String sql = "UPDATE members SET is_active = true WHERE id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
        }catch (SQLException e) {
            throw new DatabaseException("Could not activate member", e);
        }
    }

    @Override
    public List<Member> findInactiveMembers() {
        String sql = "SELECT * FROM members WHERE is_active = false";
        List<Member> members = new ArrayList<>();
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                members.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Could not find members", e);
        }

        return members;
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        return new Member(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getDate("register_date").toLocalDate(),
                rs.getBoolean("is_active")
        );
    }
}
