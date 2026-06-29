/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main4;

/**
 *
 * @author Munzer
 */
    import java.util.*;
import java.io.*;

public class LibraryManager {
    private AVLTree bookTree;
    private final String FILE_NAME = "books_data.txt";

    public LibraryManager() {
        bookTree = new AVLTree();
        loadFromFile(); 
    }

    public AVLTree getBookTree() {
        return bookTree;
    }

    public boolean addBook(int isbn, String title, String author, int copies) {
        if (bookTree.search(isbn) != null) return false; 
        bookTree.insert(new Book(isbn, title, author, copies));
        saveToFile(); 
        return true;
    }

    public boolean updateCopies(int isbn, int newCopies) {
        Book book = bookTree.search(isbn);
        if (book == null) return false;
        int borrowedCount = book.totalCopies - book.availableCopies;
        if (newCopies < borrowedCount) return false; 
        book.availableCopies = newCopies - borrowedCount;
        book.totalCopies = newCopies;
        saveToFile(); 
        return true;
    }

    public boolean deleteBook(int isbn) {
        Book book = bookTree.search(isbn);
        if (book == null) return false;
        bookTree.delete(isbn);
        saveToFile(); 
        return true;
    }

    public String borrowBook(int isbn, String studentName, boolean isGraduate, String bDate, String rDate) {
        Book book = bookTree.search(isbn);
        if (book == null) return "Error: Book not found in the system!";

        int studentBorrowCount = 0;
        for (Book b : bookTree.getAllBooks()) {
            for (BorrowRecord record : b.borrowRecords) {
                if (record.borrowerName.equalsIgnoreCase(studentName)) {
                    studentBorrowCount++;
                }
            }
        }
        if (studentBorrowCount >= 2) {
            return "Transaction Denied: Student has reached the maximum borrowing limit (2 books).";
        }

        if (book.availableCopies > 0) {
            book.availableCopies--;
            book.borrowCount++;
            book.borrowRecords.add(new BorrowRecord(studentName, bDate, rDate));
            saveToFile(); 
            return "Success: Book borrowed successfully.";
        } else {
            book.waitingQueue.add(new Student(studentName, isGraduate));
            Collections.sort(book.waitingQueue); 
            return "Notice: Book out of stock. Student added to the Priority Waiting Queue.";
        }
    }

    public String returnBook(int isbn, String studentName) {
        Book book = bookTree.search(isbn);
        if (book == null) return "Error: Book not found!";

        boolean recordFound = false;
        for (BorrowRecord record : book.borrowRecords) {
            if (record.borrowerName.equalsIgnoreCase(studentName)) {
                book.borrowRecords.remove(record);
                recordFound = true;
                break;
            }
        }

        if (!recordFound) return "Error: No active borrow record found for this student.";

        if (!book.waitingQueue.isEmpty()) {
            Student nextStudent = book.waitingQueue.remove(0); 
            book.borrowCount++;
            book.borrowRecords.add(new BorrowRecord(nextStudent.name, "Today", "In 2 Weeks"));
            saveToFile(); 
            return "Success: Book returned. Automatically assigned to waiting priority student: " + nextStudent.name;
        } else {
            book.availableCopies++;
            saveToFile(); 
            return "Success: Book returned to shelf. Available stock updated.";
        }
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Book b : bookTree.getAllBooks()) {
                writer.println(b.isbn + ";" + b.title + ";" + b.author + ";" + b.totalCopies + ";" + b.availableCopies + ";" + b.borrowCount);
            }
        } catch (IOException e) {
            System.out.println("File write error: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                if (tokens.length >= 6) {
                    int isbn = Integer.parseInt(tokens[0]);
                    String title = tokens[1];
                    String author = tokens[2];
                    int totalCopies = Integer.parseInt(tokens[3]);
                    int availableCopies = Integer.parseInt(tokens[4]);
                    int borrowCount = Integer.parseInt(tokens[5]);

                    Book book = new Book(isbn, title, author, totalCopies);
                    book.availableCopies = availableCopies;
                    book.borrowCount = borrowCount;
                    
                    bookTree.insert(book);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("File read error: " + e.getMessage());
        }
    }

    public String generateReports() {
        List<Book> allBooks = bookTree.getAllBooks();
        if (allBooks.isEmpty()) return "No data available to generate statistical reports.";

        StringBuilder report = new StringBuilder();
        int totalAvailable = 0;
        for (Book b : allBooks) totalAvailable += b.availableCopies;
        report.append("==================================================\n");
        report.append("        DIGITAL LIBRARY ANALYTICAL REPORT         \n");
        report.append("==================================================\n");
        report.append("• Total Physical Copies Available on Shelves: ").append(totalAvailable).append("\n\n");

        report.append("• Top Most Borrowed Books:\n");
        List<Book> sortedForReport = new ArrayList<>(allBooks);
        sortedForReport.sort((b1, b2) -> Integer.compare(b2.borrowCount, b1.borrowCount));
        for (int i = 0; i < Math.min(5, sortedForReport.size()); i++) {
            Book b = sortedForReport.get(i);
            report.append("  - ").append(b.title).append(" (ISBN: ").append(b.isbn).append(") -> Borrowed ").append(b.borrowCount).append(" times.\n");
        }

        report.append("\n• Most Read Authors (Total Reader Engagement):\n");
        Map<String, Integer> authorMap = new HashMap<>();
        for (Book b : allBooks) {
            authorMap.put(b.author, authorMap.getOrDefault(b.author, 0) + b.borrowCount);
        }
        authorMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> report.append("  - ").append(entry.getKey()).append(" -> Total Reads: ").append(entry.getValue()).append("\n"));

        report.append("==================================================");
        return report.toString();
    }
}