/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main4;

/**
 *
 * @author Munzer
 */
import java.util.ArrayList;
import java.util.List;

class BorrowRecord {
    String borrowerName;
    String borrowDate;
    String expectedReturnDate;

    public BorrowRecord(String borrowerName, String borrowDate, String expectedReturnDate) {
        this.borrowerName = borrowerName;
        this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate;
    }
}

class Student implements Comparable<Student> {
    String name;
    boolean isGraduate; 

    public Student(String name, boolean isGraduate) {
        this.name = name;
        this.isGraduate = isGraduate;
    }

    @Override
    public int compareTo(Student other) {
        if (this.isGraduate && !other.isGraduate) return -1;
        if (!this.isGraduate && other.isGraduate) return 1;
        return 0; 
    }
}