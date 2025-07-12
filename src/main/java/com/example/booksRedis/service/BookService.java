package com.example.booksRedis.service;

import com.example.booksRedis.model.Book;
import com.example.booksRedis.repo.BookRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BookService {

    private final CasheService casheService;
    private final BookRepo bookRepo;

    public Book getBook(Long id) {
        final String key = "book:" + id;

        casheService.incrementBookPopularity(id);

        Book cashedBook = (Book) casheService.getCashedObject(key);
        if (cashedBook != null) {
            return cashedBook;
        }
        Optional<Book> book = bookRepo.findById(id);

        book.ifPresent(
                b -> {
                    casheService.caseObject(key, b, 10L, TimeUnit.MINUTES);
                }
        );


        return book.orElse(null);
    }

    public Book addBook(Book book) {
        try{
            Book savedBook = bookRepo.save(book);
            final String key = "book:" + savedBook.getId();
            casheService.caseObject(key, savedBook, 10L, TimeUnit.MINUTES);
            return savedBook;
        }catch (Exception e){
            System.err.println("Ошибка при добавлении книги: " + e.getMessage());
            e.printStackTrace();
            return null;        }
    }

    public String deleteBook(Long id){
        try{
            final String key = "book:" + id;
            casheService.deleteCashedObject(key);

            if (bookRepo.existsById(id)){
                bookRepo.deleteById(id);
                return "Book deleted: "+id;
            }
            else return "Book not found: "+id;
        }catch (Exception e){
            return "Error deleting book: "+e.getMessage();
        }
    }

    public Optional<Book> updateBook (Book book, Long id){
        final String key = "book:" + id;

       return bookRepo.findById(id).map(b->{
           casheService.deleteCashedObject(key);
           book.setId(id);
           Book savedBook = bookRepo.save(book);
           casheService.caseObject(key, savedBook, 10L, TimeUnit.MINUTES);
           return savedBook;
       });
    }
}
