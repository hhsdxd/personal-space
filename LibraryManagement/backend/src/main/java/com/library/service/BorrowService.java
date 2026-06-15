package com.library.service;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;

    public BorrowService(BorrowRecordRepository borrowRecordRepository, BookRepository bookRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BorrowRecord borrow(User user, Long bookId) {
        if (borrowRecordRepository.existsByBookIdAndStatus(bookId, "BORROWED")) {
            throw new RuntimeException("该图书已被借出");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));
        if (!book.getAvailable()) {
            throw new RuntimeException("该图书不可借");
        }
        book.setAvailable(false);
        bookRepository.save(book);

        BorrowRecord record = new BorrowRecord(user, book);
        return borrowRecordRepository.save(record);
    }

    @Transactional
    public BorrowRecord returnBook(Long bookId) {
        BorrowRecord record = borrowRecordRepository.findByBookIdAndStatus(bookId, "BORROWED")
                .orElseThrow(() -> new RuntimeException("未找到该图书的借阅记录"));

        record.setStatus("RETURNED");
        record.setReturnTime(LocalDateTime.now());

        Book book = record.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }

    public List<BorrowRecord> myRecords(Long userId) {
        return borrowRecordRepository.findByUserIdOrderByBorrowTimeDesc(userId);
    }
}
