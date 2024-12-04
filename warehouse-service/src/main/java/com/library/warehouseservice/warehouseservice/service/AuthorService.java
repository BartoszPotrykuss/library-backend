package com.library.warehouseservice.warehouseservice.service;

import com.library.warehouseservice.warehouseservice.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorService {
    Author addAuthor(Author author);

    List<Author> getAllAuthors();
}
