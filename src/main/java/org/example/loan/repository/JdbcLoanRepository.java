package org.example.loan.repository;

import org.example.exception.DatabaseException;
import org.example.loan.Loan;
import org.example.loan.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcLoanRepository implements LoanRepository {
    private final static Logger log = LoggerFactory.getLogger(JdbcLoanRepository.class);
    private final DataSource dataSource;

    public JdbcLoanRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void save(Loan loan) {
        String sql = "INSERT INTO loans (member_id, book_id, borrow_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, loan.getMemberId());
            ps.setInt(2, loan.getBookId());
            ps.setDate(3, Date.valueOf(loan.getBorrowDate()));
            ps.setDate(4, Date.valueOf(loan.getDueDate()));
            ps.setString(5, loan.getStatus().name());

            ps.executeUpdate();
        }catch (SQLException e){
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("insert loan failed", e);
        }
    }

    @Override
    public Optional<Loan> findById(int id) {
        String sql = "SELECT * FROM loans WHERE id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("find loan by id failed", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Loan> findByMemberId(int memberId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE member_id = ?";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                loans.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("find loan by member id failed", e);
        }

        return loans;
    }

    @Override
    public List<Loan> findByBookId(int bookId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE book_id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, bookId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                loans.add(mapRow(rs));
            }
        }catch (SQLException e) {
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("find loan by book id failed", e);
        }

        return loans;
    }

    @Override
    public Optional<Loan> findActiveByMemberAndBook(int memberId, int bookId) {
        String sql = "SELECT * FROM loans WHERE member_id = ? AND book_id = ? AND status = 'ACTIVE'";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, memberId);
            ps.setInt(2, bookId);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("find loan by member and loan book id failed", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Loan> findAll() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                loans.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("find all loans failed", e);
        }

        return loans;
    }

    @Override
    public List<Loan> findOverdue() {
        List <Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE status = 'OVERDUE'";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                loans.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("find all loans failed", e);
        }

        return loans;
    }

    @Override
    public List<Loan> findActive() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE status = 'ACTIVE'";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                loans.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("find all active loans failed", e);
        }

        return loans;
    }

    @Override
    public List<Loan> findReturned(){
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE status = 'RETURNED'";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                loans.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("find all returned loans failed", e);
        }

        return loans;
    }

    @Override
    public void returnLoan(int loanId) {
        String sql = "UPDATE loans SET status = 'RETURNED', return_date = ? WHERE id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, loanId);

            ps.executeUpdate();
        }catch (SQLException e){
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("update loan failed", e);
        }
    }


    private Loan mapRow(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setMemberId(rs.getInt("member_id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setBorrowDate(rs.getDate("borrow_date").toLocalDate());

        LocalDate dueDate = rs.getDate("due_date").toLocalDate();
        loan.setDueDate(dueDate);

        Status status = Status.valueOf(rs.getString("status"));
        if(status == Status.ACTIVE && dueDate.isBefore(LocalDate.now())){
            status = Status.OVERDUE;
        }
        loan.setStatus(status);

        Date returnDate = rs.getDate("return_date");
        loan.setReturnDate(returnDate != null ? returnDate.toLocalDate() : null);

        return loan;
    }
}
