package org.example.controller;

import org.example.book.BookService;
import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookResponse;
import org.example.book.dto.BookUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody BookCreateRequest request) {
        bookService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> findAll() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<BookResponse>> findByTitle(@PathVariable String title) {
        return ResponseEntity.ok(bookService.findByTitle(title));
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<BookResponse>> findByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(bookService.findByAuthor(author));
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<BookResponse>> findAllInactive() {
        return ResponseEntity.ok(bookService.findAllInactive());
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody BookUpdateRequest request) {
        bookService.update(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/activate/{id}")
    public ResponseEntity<Void> activate(@PathVariable int id){
        bookService.activate(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id){
        bookService.deactivate(id);
        return ResponseEntity.ok().build();
    }
}
