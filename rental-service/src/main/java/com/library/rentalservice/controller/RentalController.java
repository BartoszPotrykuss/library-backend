package com.library.rentalservice.controller;

import com.library.rentalservice.dto.BookRequest;
import com.library.rentalservice.entity.Rental;
import com.library.rentalservice.service.RentalService;
import com.library.rentalservice.service.RentalServiceImpl;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;
    public RentalController(RentalServiceImpl rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping("/rent")
    public ResponseEntity<List<Rental>> getActiveRentals(HttpServletRequest request) {
        List<Rental> rentals = rentalService.getActiveRentals(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rentals);
    }

//    @GetMapping("/rent")
//    public ResponseEntity<List<Rental>> getAllRentals(HttpServletRequest request) {
//        List<Rental> rentals = rentalService.getAllRentals(request);
//        return ResponseEntity.status(HttpStatus.OK).body(rentals);
//    }

    @RateLimiter(name = "rental")
    @PostMapping("/rent")
    public ResponseEntity<?> rentBook(@RequestBody BookRequest bookRequest, HttpServletRequest request) {
       Rental rental = rentalService.rentBook(bookRequest, request);
        return ResponseEntity.ok(rental);
    }

    @RateLimiter(name = "rental")
    @PatchMapping("/return/{id}")
    public ResponseEntity<?> returnBook(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long additionalFee = rentalService.returnBook(id, request);
        String message;

        if (additionalFee.equals(0L)) {
            message = "Oddano książkę w terminie.";
        } else {
            message = "Oddano książkę " + additionalFee + " dni po terminie. Kwota do zapłacenia: " + additionalFee + " PLN";
        }

        return ResponseEntity.ok(message);
    }

    @GetMapping("/rent/report")
    public ResponseEntity<byte[]> generateBookReport() {
        byte[] pdfReport = rentalService.generateRentalReport();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rental_report.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);


        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfReport);
    }
}
