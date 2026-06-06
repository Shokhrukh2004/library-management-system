package org.example.book.repository;


import org.example.book.Book;
import org.example.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class JdbcBookRepository implements BookRepository {
    private final static Logger log = LoggerFactory.getLogger(JdbcBookRepository.class);
    private final DataSource dataSource;

    public JdbcBookRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, total_copies, available_copies, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setInt(4, book.getTotalCopies());
            ps.setInt(5, book.getAvailableCopies());
            ps.setBoolean(6, book.isActive());

            ps.executeUpdate();

        }catch (SQLException e){
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("Failed to save book", e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public Optional<Book> findById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return Optional.of(mapRow(rs));
            }
        }catch (SQLException e){
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("Failed to find book", e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT * FROM books WHERE is_active = true";
        List<Book> books = new ArrayList<>();
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){


            while(rs.next()){
                books.add(mapRow(rs));
            }
        }catch (SQLException e){
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("Failed to find books", e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        return books;
    }

    @Override
    public List<Book> findByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title ILIKE ? AND is_active = true";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + title + "%");

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                books.add(mapRow(rs));
            }

        }catch (SQLException e){
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("Failed to find books by title", e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        return books;
    }

    @Override
    public List<Book> findByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author ILIKE ? AND is_active = true";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + author + "%");

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                books.add(mapRow(rs));
            }
        }catch (SQLException e){
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("Failed to find books by author", e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        return books;
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return Optional.of(mapRow(rs));
            }
        }catch (SQLException e){
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("Failed to find books by isbn", e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        return Optional.empty();
    }

    @Override
    public void update(Book book) {
        String sql = "UPDATE books SET total_copies = ?, available_copies = ? WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, book.getTotalCopies());
            ps.setInt(2, book.getAvailableCopies());
            ps.setInt(3, book.getId());

            ps.executeUpdate();
        }catch (SQLException e){
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("Failed to update book", e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public void deactivate(int id) {
        String sql = "UPDATE books SET is_active = false WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ps.executeUpdate();

        }catch (SQLException e){
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("Failed to deactivate book", e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public void activate(int id) {
        String sql = "UPDATE books SET is_active = true WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

           ps.executeUpdate();

        }catch (SQLException e){
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("Failed to activate book", e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public List<Book> findAllInactive() {
        String sql = "SELECT * FROM books WHERE is_active = false";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        List<Book> books = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                books.add(mapRow(rs));
            }
        }catch (SQLException e){
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("Failed to find books", e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        return books;
    }

    @Override
    public void increaseAvailableCopies(int id) {
        String sql = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("Failed to update books", e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public void decreaseAvailableCopies(int id) {
        String sql = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Database Error Occurred: ",  e);
            throw new DatabaseException("Failed to update books", e);
        }finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getInt("total_copies"),
                rs.getInt("available_copies"),
                rs.getBoolean("is_active")
        );
    }
}
