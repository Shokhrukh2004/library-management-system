package org.example.book;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Setter
    @Column(nullable = false, name = "total_copies")
    private int totalCopies;

    @Setter
    @Column(nullable = false, name = "available_copies")
    private int availableCopies;

    @Setter
    @Column(nullable = false, name = "is_active")
    private boolean isActive;
}
