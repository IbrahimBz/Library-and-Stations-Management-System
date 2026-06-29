package org.library;

import java.util.ArrayList;
import java.util.List;

    public class Book {
    int isbn;
    String title;
    String author;
    int totalCopies;
    int availableCopies;
    int borrowCount; 
    
    List<BorrowRecord> borrowRecords; 
    List<Student> waitingQueue; 

    public Book(int isbn, String title, String author, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.borrowCount = 0;
        this.borrowRecords = new ArrayList<>();
        this.waitingQueue = new ArrayList<>(); 
    }
}
