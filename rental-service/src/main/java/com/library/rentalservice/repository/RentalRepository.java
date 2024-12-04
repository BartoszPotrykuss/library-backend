package com.library.rentalservice.repository;

import com.library.rentalservice.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    Optional<Rental> findByUsernameAndBookTitle(String username, String bookTitle);
    List<Rental> getRentalsByUsername(String username);

    List<Rental> getRentalsByIsReturned(Boolean isReturned);

    // Metody do raportu
    long countByIsReturnedFalse(); // Liczba wypożyczeń aktualnie niezwróconych

    long countByIsReturnedTrue(); // Liczba wypożyczeń zwróconych
    List<Rental> findByEndDateBeforeAndIsReturnedFalse(LocalDate date); // Wypożyczenia przeterminowane
    @Query("SELECT r.bookTitle, COUNT(r) AS cnt FROM Rental r GROUP BY r.bookTitle ORDER BY cnt DESC")
    List<Object[]> findMostPopularBooks(); // Najpopularniejsze książki z licznikami wypożyczeń

    @Query("SELECT r.username, COUNT(r) AS cnt FROM Rental r GROUP BY r.username ORDER BY cnt DESC")
    List<Object[]> findMostActiveUsers(); // Najaktywniejsi użytkownicy z licznikami wypożyczeń

    @Query("SELECT COUNT(r) FROM Rental r WHERE r.startDate BETWEEN :start AND :end")
    long countRentalsBetweenDates(@Param("start") LocalDate start, @Param("end") LocalDate end); // Wypożyczenia w podanym okresie
}
