package com.library.warehouseservice.warehouseservice.repository;

import com.library.warehouseservice.warehouseservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface BookRepository extends JpaRepository<Book, Long> {
    Book getBookByTitle(String title);

    @Query("SELECT SUM(b.quantity) FROM Book b")
    Long getTotalNumberOfBooks();

    @Query("SELECT COUNT(b) FROM Book b")
    Long getUniqueBookCount();

    @Query("SELECT AVG(b.quantity) FROM Book b")
    Double getAverageQuantityPerTitle();

    @Query("SELECT b FROM Book b WHERE b.quantity = (SELECT MAX(b2.quantity) FROM Book b2)")
    List<Book> getBookWithMaxQuantity();

    @Query("SELECT b.genre, SUM(b.quantity) FROM Book b GROUP BY  b.genre ORDER BY SUM(b.quantity) DESC")
    List<Object[]> getMostPopularGenres();

    @Query("SELECT b.author, SUM(b.quantity) FROM Book b GROUP BY b.author ORDER BY SUM(b.quantity) DESC")
    List<Object[]> getAuthorWithMostBooks();

    @Query("SELECT b FROM Book b WHERE b.quantity = (SELECT MIN(b2.quantity) FROM Book b2)")
    List<Book> getBookWithMinQuantity();

    @Query("SELECT b.genre, SUM(b.quantity) FROM Book b GROUP BY b.genre ORDER BY SUM(b.quantity) ASC")
    List<Object[]> getLeastPopularGenres();

    @Query("SELECT b.genre, (SUM(b.quantity) * 1.0 / (SELECT SUM(b2.quantity) FROM Book b2)) * 100 FROM Book b GROUP BY b.genre")
    List<Object[]> getGenreDistributionInPercentages();

    @Query("SELECT b.title, (b.quantity * 1.0 / (SELECT AVG(b2.quantity) FROM Book b2)) * 100 FROM Book b")
    List<Object[]> getBooksAboveOrBelowAverageQuantity();

    @Query("SELECT b.author, COUNT(b) FROM Book b GROUP BY b.author ORDER BY COUNT(b) DESC")
    List<Object[]> getMostFrequentAuthor();

}
