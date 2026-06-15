package com.student.controller;

import com.student.dto.ApiResponse;
import com.student.entity.Student;
import com.student.service.StudentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ApiResponse<?> list(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return ApiResponse.success(studentService.search(keyword));
        }
        return ApiResponse.success(studentService.listAll());
    }

    @GetMapping("/stats")
    public ApiResponse<?> stats() {
        return ApiResponse.success(studentService.stats());
    }

    @PostMapping
    public ApiResponse<?> add(@RequestBody Student student) {
        try {
            return ApiResponse.success("添加成功", studentService.add(student));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @RequestBody Student student) {
        try {
            return ApiResponse.success("更新成功", studentService.update(id, student));
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        try {
            studentService.delete(id);
            return ApiResponse.success("删除成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
