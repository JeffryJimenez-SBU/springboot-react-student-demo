package com.example.demo.student;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.StudentNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach
    void setUp() {

        underTest = new StudentService(studentRepository);

    }


    @Test
    void canGetAllStudent() {

        //when
        underTest.getAllStudent();

        //then
        verify(studentRepository).findAll();


    }

    @Test
    void canAddStudent() {

        //given
        Student student = new Student(
                "John",
                "john@mail.com",
                Gender.MALE
        );

        //when
        underTest.addStudent(student);

        //then (check that the repository was called with the same arguent we pased)
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);

        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student capturedStudent = studentArgumentCaptor.getValue();

        assertThat(capturedStudent).isEqualTo(student);
    }

    @Test
    void willThrowWhenEmailIsTaken() {

        //given
        Student student = new Student(
                "John",
                "john@mail.com",
                Gender.MALE
        );

        //force the method to return true!
        given(studentRepository.selectExistsEmail(student.getEmail()))
                .willReturn(true);

        //when
        //then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        //verify student never get saved
        verify(studentRepository, never()).save(any());


    }

    @Test
    void deleteStudent() {

        //given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(true);

        //when
        underTest.deleteStudent(id);

        //then
        verify(studentRepository).deleteById(id);
    }

    @Test
    void willThrowWhenDeleteStudent() {

        //given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(false);

        //when
        //then
        assertThatThrownBy(() -> underTest.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exits");

        verify(studentRepository, never()).deleteById(any());
    }
}