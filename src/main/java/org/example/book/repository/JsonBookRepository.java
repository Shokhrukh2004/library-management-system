package org.example.book.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.book.Book;

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
    public void save(Book book){
        book.setId(getId());
        books.add(book);
    }

    @Override
    public Optional<Book> findById(int id) {
        return books.stream()
                .filter(book -> book.getId() == id)
                .findFirst();
    }

    @Override
    public List<Book> findAll() {

        return books.stream()
                .filter(Book::isActive)
                .toList();
    }

    @Override
    public List<Book> findByTitle(String title) {
        return books.stream()
                .filter(Book::isActive)
                .filter(book -> book.getTitle()
                        .contains(title))
                .toList();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return books.stream()
                .filter(Book::isActive)
                .filter(book -> book.getAuthor()
                        .contains(author))
                .toList();
    }

    @Override
    public void update(Book book) {
    }

    @Override
    public void activate(Book book){
        book.setActive(true);
    }

    @Override
    public void deactivate(Book book) {
        book.setActive(false);
    }

    @Override
    public List<Book> findAllInactive(){
        return books.stream()
                .filter(book -> !book.isActive())
                .toList();
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
