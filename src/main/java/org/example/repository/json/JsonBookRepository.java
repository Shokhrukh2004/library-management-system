package org.example.repository.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.model.Book;
import org.example.repository.BookRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonBookRepository implements BookRepository {

    private final ObjectMapper mapper;
    private final String filePath;
    private final List<Book> books;
    private int availableId;

    public JsonBookRepository(ObjectMapper mapper, String filePath) {
        this.mapper = mapper;
        this.filePath = filePath;
        this.books = new ArrayList<>();
        this.availableId = 0;
    }

    @Override
    public void save(Book book) {
        book.setId(getId());
        this.books.add(book);
    }

    @Override
    public Optional<Book> findById(int id) {

        return books.stream()
                .filter(book -> book.getId() == id)
                .findFirst();
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books);
    }

    @Override
    public List<Book> findByTitle(String title) {

        return books.stream()
                .filter(book -> book.getTitle()
                        .toLowerCase()
                        .contains(title.toLowerCase()))
                .toList();
    }

    @Override
    public List<Book> findByAuthor(String author) {

        return books.stream()
                .filter(book -> book.getAuthor()
                        .toLowerCase()
                        .contains(author.toLowerCase()))
                .toList();
    }

    @Override
    public void update(Book book) {
        findById(book.getId())
                .ifPresent(existingBook -> {
                    existingBook.setTotalCopies(book.getTotalCopies());
                    existingBook.setAvailableCopies(book.getAvailableCopies());
                });
    }

    @Override
    public void delete(int id) {
        books.removeIf(book -> book.getId() == id);

    }

    @PostConstruct
    public void load(){
        try{
            File file = new File(filePath);
            if(!file.exists()){
                return;
            }
            List<Book> loaded = mapper.readValue(
                    file,
                    new TypeReference<List<Book>>(){}
            );
            books.addAll(loaded);

            availableId = books.stream()
                    .mapToInt(Book::getId)
                    .max()
                    .orElse(0);

        }catch (IOException e){
            System.out.println("Could not load json file: " + e.getMessage());
        }
    }


    @PreDestroy
    public void saveEnd(){
        try{
            File file = new File(filePath);
            mapper.writeValue(file, books);
        }catch (IOException e){
            System.out.println("Could not save json file: " + e.getMessage());
        }
    }

    private int getId(){
       return ++availableId;
    }
}
