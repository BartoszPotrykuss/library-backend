package com.library.warehouseservice.warehouseservice.service;

import com.library.warehouseservice.warehouseservice.dto.BookResponse;
import com.library.warehouseservice.warehouseservice.model.Book;

import java.util.List;

public interface BookService {
    Book addBook(Book book);
    List<Book> getAllBooks();

    BookResponse getBookByTitle(String title);

    void removeOneQuantity(String title);

    void addOneQuantity(String title);

    byte[] generateBookReport();
}
