package com.library.repository;

import com.library.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUserIdOrderByBorrowTimeDesc(Long userId);
    Optional<BorrowRecord> findByBookIdAndStatus(Long bookId, String status);
    boolean existsByBookIdAndStatus(Long bookId, String status);
}
