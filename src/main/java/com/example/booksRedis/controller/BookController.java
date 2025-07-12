package com.example.booksRedis.controller;

import com.example.booksRedis.model.Book;
import com.example.booksRedis.service.BookService;
import com.example.booksRedis.service.CasheService;
import lombok.Lombok;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final CasheService casheService;

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id){
       return bookService.getBook(id);
    }

    @PostMapping("/")
    public Book addBook(@RequestBody Book book){
        return bookService.addBook(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book){
        return bookService.updateBook(book, id).get();
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable Long id){
        return bookService.deleteBook(id);
    }

    @GetMapping("/popular")
    public List<Book> getPopularBooks(){
        Set<String> topBooksIds = casheService.getPopularBookIds(10);

        List<Long> ids = topBooksIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        return ids.stream()
                .map(this::getBookById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


}


