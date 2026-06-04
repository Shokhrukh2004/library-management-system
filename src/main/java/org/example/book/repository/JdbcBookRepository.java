package org.example.book.repository;

import org.example.book.Book;
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
    private final DataSource dataSource;

    public JdbcBookRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, total_copies, available_copies, is_active) VALUES (?, ?, ?, ?, ?, ?)";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setInt(4, book.getTotalCopies());
            ps.setInt(5, book.getAvailableCopies());
            ps.setBoolean(6, book.isActive());

            ps.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Failed to save book", e);
        }
    }

    @Override
    public Optional<Book> findById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return Optional.of(mapRow(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Failed to find book", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT * FROM books WHERE is_active = true";
        List<Book> books = new ArrayList<>();

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){


            while(rs.next()){
                books.add(mapRow(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Failed to find books", e);
        }

        return books;
    }

    @Override
    public List<Book> findByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title ILIKE ? AND is_active = true";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + title + "%");

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                books.add(mapRow(rs));
            }

        }catch (SQLException e){
            throw new RuntimeException("Failed to find books by title", e);
        }

        return books;
    }

    @Override
    public List<Book> findByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author ILIKE ? AND is_active = true";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + author + "%");

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                books.add(mapRow(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Failed to find books by author", e);
        }

        return books;
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return Optional.of(mapRow(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Failed to find books by isbn", e);
        }

        return Optional.empty();
    }

    @Override
    public void update(Book book) {
        String sql = "UPDATE books SET total_copies = ?, available_copies = ? WHERE id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, book.getTotalCopies());
            ps.setInt(2, book.getAvailableCopies());
            ps.setInt(3, book.getId());

            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Failed to update book", e);
        }
    }

    @Override
    public void deactivate(int id) {
        String sql = "UPDATE books SET is_active = false WHERE id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            ps.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Failed to deactivate book", e);
        }
    }

    @Override
    public void activate(int id) {
        String sql = "UPDATE books SET is_active = true WHERE id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

           ps.executeUpdate();

        }catch (SQLException e){
            throw new  RuntimeException("Failed to activate book", e);
        }
    }

    @Override
    public List<Book> findAllInactive() {
        String sql = "SELECT * FROM books WHERE is_active = false";
        List<Book> books = new ArrayList<>();
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                books.add(mapRow(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Failed to find books", e);
        }
        return books;
    }

    @Override
    public void increaseAvailableCopies(int id) {
        String sql = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update books", e);
        }
    }

    @Override
    public void decreaseAvailableCopies(int id) {
        String sql = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update books", e);
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
