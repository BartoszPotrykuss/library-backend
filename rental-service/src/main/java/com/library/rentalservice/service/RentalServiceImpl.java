package com.library.rentalservice.service;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.library.rentalservice.dto.BookResponse;
import com.library.rentalservice.dto.BookRequest;
import com.library.rentalservice.entity.Rental;
import com.library.rentalservice.repository.RentalRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final WebClient webClient;

    public RentalServiceImpl(RentalRepository rentalRepository, WebClient webClient) {
        this.rentalRepository = rentalRepository;
        this.webClient = webClient;
    }

    @Override
    public List<Rental> getActiveRentals(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        Claims claims = extractClaimsFromJwtToken(authorizationHeader);
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        if (role.equals("USER")) {
            return rentalRepository.getRentalsByUsername(username);
        }
        else if (role.equals("ADMIN")) {
            return rentalRepository.findAll();
        }
        throw new RuntimeException("That role does not exist");
    }

    @Override
    public byte[] generateRentalReport() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            String fontPath = "fonts/Arial.ttf";
            PdfFont font = PdfFontFactory.createFont(fontPath, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

            // Tworzenie PdfWriter i PdfDocument
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            document.setFont(font);

            // Tytuł raportu
            document.add(new Paragraph("Raport Wypożyczeń")
                    .setFont(font)
                    .setBold()
                    .setFontSize(16)
                    .setMarginBottom(20));

            // Pobranie danych statystycznych
            long activeRentals = rentalRepository.countByIsReturnedFalse();
            long returnedRentals = rentalRepository.countByIsReturnedTrue();
            List<Rental> overdueRentals = rentalRepository.findByEndDateBeforeAndIsReturnedFalse(LocalDate.now());
            List<Object[]> mostPopularBooks = rentalRepository.findMostPopularBooks();
            List<Object[]> mostActiveUsers = rentalRepository.findMostActiveUsers();

            // Dodanie statystyk ogólnych
            document.add(new Paragraph("Statystyki Ogólne")
                    .setFont(font)
                    .setBold()
                    .setFontSize(14)
                    .setMarginBottom(10));
            document.add(new Paragraph("Aktywne wypożyczenia: " + activeRentals));
            document.add(new Paragraph("Zwrócone wypożyczenia: " + returnedRentals));
            document.add(new Paragraph("Przeterminowane wypożyczenia: " + overdueRentals.size()));

            // Najpopularniejsze książki
            document.add(new Paragraph("\nNajpopularniejsze książki")
                    .setFont(font)
                    .setBold()
                    .setFontSize(14)
                    .setMarginBottom(10));
            if (!mostPopularBooks.isEmpty()) {
                for (int i = 0; i < Math.min(mostPopularBooks.size(), 5); i++) { // Top 5
                    Object[] record = mostPopularBooks.get(i);
                    String bookTitle = (String) record[0];
                    long count = (long) record[1];
                    document.add(new Paragraph((i + 1) + ". " + bookTitle + " - " + count + " wypożyczeń"));
                }
            } else {
                document.add(new Paragraph("Brak danych."));
            }

            // Najaktywniejsi użytkownicy
            document.add(new Paragraph("\nNajaktywniejsi użytkownicy")
                    .setFont(font)
                    .setBold()
                    .setFontSize(14)
                    .setMarginBottom(10));
            if (!mostActiveUsers.isEmpty()) {
                for (int i = 0; i < Math.min(mostActiveUsers.size(), 5); i++) { // Top 5
                    Object[] record = mostActiveUsers.get(i);
                    String username = (String) record[0];
                    long count = (long) record[1];
                    document.add(new Paragraph((i + 1) + ". " + username + " - " + count + " wypożyczeń"));
                }
            } else {
                document.add(new Paragraph("Brak danych."));
            }

            // Wypożyczenia w bieżącym miesiącu
            document.add(new Paragraph("\nWypożyczenia w bieżącym miesiącu")
                    .setFont(font)
                    .setBold()
                    .setFontSize(14)
                    .setMarginBottom(10));
            LocalDate now = LocalDate.now();
            LocalDate startOfMonth = now.withDayOfMonth(1);
            LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
            long rentalsThisMonth = rentalRepository.countRentalsBetweenDates(startOfMonth, endOfMonth);

            document.add(new Paragraph("Liczba wypożyczeń: " + rentalsThisMonth));

            // Zamknięcie dokumentu
            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas generowania raportu PDF", e);
        }

        return byteArrayOutputStream.toByteArray();
    }



    @Override
    public List<Rental> getAllRentals(HttpServletRequest request) {
        return rentalRepository.findAll();
    }

    @Override
    public Rental rentBook(BookRequest bookRequest, HttpServletRequest request)  {
        Rental rental = new Rental();
        String title = bookRequest.getTitle();

        //String token = authorizationHeader.substring(7);
//        String[] chunks = token.split("\\.");
//        Base64.Decoder decoder = Base64.getUrlDecoder();
//        String payload = new String(decoder.decode(chunks[1]));
//        rental.setUsername(payload);

        String authorizationHeader = request.getHeader("Authorization");
        Claims claims = extractClaimsFromJwtToken(authorizationHeader);
        String username = claims.getSubject();
        String role = claims.get("role", String.class);


        Optional<Rental> optionalRental = rentalRepository.findByUsernameAndBookTitle(username, title);
        if (optionalRental.isPresent() && !optionalRental.get().getIsReturned()) {
            throw new RuntimeException("You've already borrowed this book");
        }
        rental.setUsername(username);


        BookResponse bookResponse = webClient
                .get()
                .uri("http://localhost:8080/api/book/" + title)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .bodyToMono(BookResponse.class)
                .block();

        if (bookResponse == null) {
            throw new IllegalArgumentException("Book not found");
        } else {
            if (bookResponse.getQuantity().equals(0L)) {
                throw new IllegalArgumentException("Every book titled "+ bookResponse.getTitle() +" has already been borrowed. Try again later");
            }
            else {
                rental.setBookTitle(bookResponse.getTitle());
                webClient
                        .method(HttpMethod.PATCH)
                        .uri("http://localhost:8080/api/book/" + title + "/removeQuantity")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                webClient
                        .method(HttpMethod.PATCH)
                        .uri("http://localhost:8080/auth/api/user/username/" + username + "/wallet/" + 10L)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                return rentalRepository.save(rental);
            }
        }
    }

    @Override
    @Transactional
    public Long returnBook(Long id, HttpServletRequest request) {

        Optional<Rental> rentalOptional = rentalRepository.findById(id);
        Rental rental = rentalOptional.orElse(null);

        String authorizationHeader = request.getHeader("Authorization");
        Claims claims = extractClaimsFromJwtToken(authorizationHeader);
        String username = claims.getSubject();

        if (rental == null) {
            throw new IllegalArgumentException("Rental not found");
        }
        else {
            long additionalFee = 0L;
            if(isDeadLineExceeded(rental)) {
                additionalFee = ChronoUnit.DAYS.between(rental.getEndDate(), LocalDate.now());
            }
            webClient
                    .method(HttpMethod.PATCH)
                    .uri("http://localhost:8080/api/book/" + rental.getBookTitle() + "/addQuantity")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            if (additionalFee != 0L) {
                webClient
                        .method(HttpMethod.PATCH)
                        .uri("http://localhost:8080/auth/api/user/username/" + username + "/wallet/" + additionalFee)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            }
            rental.setIsReturned(true);
            rentalRepository.save(rental);
            return additionalFee;
        }
    }

    private boolean isDeadLineExceeded(Rental rental) {
        return LocalDate.now().isAfter(rental.getEndDate());
    }

    private Claims extractClaimsFromJwtToken(String authorizationHeader) {
        // Wyciągnij token JWT z nagłówka
        String jwtToken = authorizationHeader.substring(7);

        // Rozkoduj token i odczytaj dane
        return Jwts.parser()
                .setSigningKey("5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437") // Klucz symetryczny
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}
