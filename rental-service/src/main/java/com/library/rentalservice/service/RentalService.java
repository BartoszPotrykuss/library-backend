package com.library.rentalservice.service;

import com.library.rentalservice.dto.BookRequest;
import com.library.rentalservice.entity.Rental;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface RentalService {
    List<Rental> getAllRentals(HttpServletRequest request);
    Rental rentBook(BookRequest bookRequest, HttpServletRequest request);
    Long returnBook(Long id, HttpServletRequest request);
    List<Rental> getActiveRentals(HttpServletRequest request);
    byte[] generateRentalReport();
}
