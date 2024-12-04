package com.library.warehouseservice.warehouseservice.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.library.warehouseservice.warehouseservice.dto.BookResponse;
import com.library.warehouseservice.warehouseservice.model.Book;
import com.library.warehouseservice.warehouseservice.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book addBook(Book book) {

        Book bookDB = bookRepository.getBookByTitle(book.getTitle());

        if (bookDB != null) {
            bookDB.setQuantity(book.getQuantity() + bookDB.getQuantity());
            return bookRepository.save(bookDB);
        }
        else return bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public BookResponse getBookByTitle(String title) {
        Book book = bookRepository.getBookByTitle(title);
        if (book != null) {
            BookResponse bookResponse = BookResponse.builder()
                    .title(book.getTitle())
                    .quantity(book.getQuantity())
                    .genre(book.getGenre())
                    .build();
            return bookResponse;
        }
        else {
            return null;
        }
    }

    @Override
    public void removeOneQuantity(String title) {
        Book bookDB = bookRepository.getBookByTitle(title);
        bookDB.setQuantity(bookDB.getQuantity() - 1);
        bookRepository.save(bookDB);
    }

    @Override
    public void addOneQuantity(String title) {
        Book bookDB = bookRepository.getBookByTitle(title);
        bookDB.setQuantity(bookDB.getQuantity() + 1);
        bookRepository.save(bookDB);
    }

    @Override
    public byte[] generateBookReport() {
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
            document.add(new Paragraph("Raport Książek")
                    .setFont(font)
                    .setBold()
                    .setFontSize(16)
                    .setMarginBottom(20));

            // Pobranie danych statystycznych
            Long totalBooks = bookRepository.getTotalNumberOfBooks();
            Long uniqueTitles = bookRepository.getUniqueBookCount();
            Double averageQuantity = bookRepository.getAverageQuantityPerTitle();

            List<Book> booksWithMaxQuantity = bookRepository.getBookWithMaxQuantity();
            List<Object[]> mostPopularGenres = bookRepository.getMostPopularGenres();
            List<Object[]> authorWithMostBooks = bookRepository.getAuthorWithMostBooks();
            List<Book> booksWithMinQuantity = bookRepository.getBookWithMinQuantity();
            List<Object[]> leastPopularGenres = bookRepository.getLeastPopularGenres();

            // Dodanie statystyk ogólnych
            document.add(new Paragraph("Statystyki Ogólne")
                    .setFont(font)
                    .setBold()
                    .setFontSize(14)
                    .setMarginBottom(10));
            document.add(new Paragraph("Całkowita liczba książek: " + (totalBooks != null ? totalBooks : 0)));
            document.add(new Paragraph("Liczba unikalnych tytułów: " + (uniqueTitles != null ? uniqueTitles : 0)));
            document.add(new Paragraph("Średnia liczba egzemplarzy na tytuł: " + (averageQuantity != null ? averageQuantity : 0)));

            // Największe wartości
            document.add(new Paragraph("\nNajwiększe wartości")
                    .setFont(font)
                    .setBold()
                    .setFontSize(14)
                    .setMarginBottom(10));
            if (!booksWithMaxQuantity.isEmpty()) {
                Book maxBook = booksWithMaxQuantity.get(0);
                document.add(new Paragraph("Tytuł z największą liczbą egzemplarzy: " + maxBook.getTitle() + " - " + maxBook.getQuantity() + " egzemplarzy"));
            }
            if (!mostPopularGenres.isEmpty()) {
                Object[] mostPopularGenre = mostPopularGenres.get(0);
                document.add(new Paragraph("Najpopularniejszy gatunek: " + mostPopularGenre[0] + " - " + mostPopularGenre[1] + " książek"));
            }
            if (!authorWithMostBooks.isEmpty()) {
                Object[] topAuthor = authorWithMostBooks.get(0);
                document.add(new Paragraph("Autor z największą liczbą książek: " + topAuthor[0] + " - " + topAuthor[1] + " książek"));
            }

            // Najmniejsze wartości
            document.add(new Paragraph("\nNajmniejsze wartości")
                    .setFont(font)
                    .setBold()
                    .setFontSize(14)
                    .setMarginBottom(10));
            if (!booksWithMinQuantity.isEmpty()) {
                Book minBook = booksWithMinQuantity.get(0);
                document.add(new Paragraph("Tytuł z najmniejszą liczbą egzemplarzy: " + minBook.getTitle() + " - " + minBook.getQuantity() + " egzemplarzy"));
            }
            if (!leastPopularGenres.isEmpty()) {
                Object[] leastPopularGenre = leastPopularGenres.get(0);
                document.add(new Paragraph("Najmniej popularny gatunek: " + leastPopularGenre[0] + " - " + leastPopularGenre[1] + " książek"));
            }

            // Zamknięcie dokumentu
            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas generowania raportu PDF", e);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
