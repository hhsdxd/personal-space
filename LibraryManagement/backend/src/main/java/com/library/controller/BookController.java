package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.entity.Book;
import com.library.service.BookService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Book")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ApiResponse<?> list(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return ApiResponse.success(bookService.search(keyword));
        }
        return ApiResponse.success(bookService.listAll());
    }

    @GetMapping("/stats")
    public ApiResponse<?> stats() {
        long total = bookService.listAll().size();
        long available = bookService.countAvailable();
        long borrowed = bookService.countBorrowed();
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("total", total);
        stats.put("available", available);
        stats.put("borrowed", borrowed);
        return ApiResponse.success(stats);
    }

    @PostMapping
    public ApiResponse<?> add(@RequestBody Book book) {
        try {
            return ApiResponse.success("添加成功", bookService.add(book));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @RequestBody Book book) {
        try {
            return ApiResponse.success("更新成功", bookService.update(id, book));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        try {
            bookService.delete(id);
            return ApiResponse.success("删除成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
