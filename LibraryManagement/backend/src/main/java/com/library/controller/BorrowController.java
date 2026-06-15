package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.entity.User;
import com.library.service.BorrowService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/borrow")
@Tag(name = "Borrow")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping("/{bookId}")
    public ApiResponse<?> borrow(@AuthenticationPrincipal User user, @PathVariable Long bookId) {
        try {
            return ApiResponse.success("借阅成功", borrowService.borrow(user, bookId));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/return/{bookId}")
    public ApiResponse<?> returnBook(@PathVariable Long bookId) {
        try {
            return ApiResponse.success("归还成功", borrowService.returnBook(bookId));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/my")
    public ApiResponse<?> myRecords(@AuthenticationPrincipal User user) {
        return ApiResponse.success(borrowService.myRecords(user.getId()));
    }
}
