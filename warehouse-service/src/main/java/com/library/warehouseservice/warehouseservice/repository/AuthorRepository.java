package com.library.warehouseservice.warehouseservice.repository;

import com.library.warehouseservice.warehouseservice.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
