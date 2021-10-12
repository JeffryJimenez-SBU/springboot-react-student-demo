package com.example.demo.student;


import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.StudentNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAllStudent(){
        return studentRepository.findAll();
    }

    public void addStudent(Student student) {

        //check if email is taken
        Boolean existsEmail = studentRepository.selectExistsEmail(student.getEmail());

        if(existsEmail) {
            throw new BadRequestException("Email " + student.getEmail() + " taken");
        }
        studentRepository.save(student);

    }

    public void deleteStudent(Long id) {

        //check if student exists
        if(!studentRepository.existsById(id)){

            throw new StudentNotFoundException("Student with id " + id + " does not exits");

        }
        studentRepository.deleteById(id);

    }
}