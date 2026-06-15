package com.student.service;

import com.student.entity.Student;
import com.student.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> listAll() {
        return studentRepository.findAll();
    }

    public List<Student> search(String keyword) {
        return studentRepository.findByNameContainingOrStudentNoContaining(keyword, keyword);
    }

    public Student add(Student student) {
        return studentRepository.save(student);
    }

    public Student update(Long id, Student updated) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        student.setName(updated.getName());
        student.setGender(updated.getGender());
        student.setAge(updated.getAge());
        student.setStudentNo(updated.getStudentNo());
        student.setClassName(updated.getClassName());
        student.setPhone(updated.getPhone());
        student.setAddress(updated.getAddress());
        return studentRepository.save(student);
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
    }

    public Map<String, Object> stats() {
        long total = studentRepository.count();
        long male = studentRepository.countByGender("男");
        long female = studentRepository.countByGender("女");

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("male", male);
        stats.put("female", female);
        return stats;
    }
}
