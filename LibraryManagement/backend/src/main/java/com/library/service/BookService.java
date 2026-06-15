package com.library.service;

import com.library.entity.Book;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> listAll() {
        return bookRepository.findAll();
    }

    public Book add(Book book) {
        return bookRepository.save(book);
    }

    public Book update(Long id, Book updated) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("图书不存在"));
        book.setTitle(updated.getTitle());
        book.setAuthor(updated.getAuthor());
        book.setIsbn(updated.getIsbn());
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> search(String keyword) {
        return bookRepository.findByTitleContainingOrAuthorContaining(keyword, keyword);
    }

    public long countAvailable() {
        return bookRepository.countByAvailableTrue();
    }

    public long countBorrowed() {
        return bookRepository.countByAvailableFalse();
    }
}
