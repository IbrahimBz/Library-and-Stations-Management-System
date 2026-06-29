
package org.library;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class LibraryGUI extends JFrame {
    private LibraryManager manager;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JTextArea reportArea;

    // لوحة الألوان الرسمية والأنيقة (Formal & Modern Palette)
    private final Color PRIMARY_COLOR = new Color(38, 50, 56);
    private final Color ACCENT_COLOR = new Color(54, 72, 80);
    private final Color BG_LIGHT = new Color(248, 249, 250);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color CARD_BG = Color.WHITE;

    public LibraryGUI() {
        manager = new LibraryManager();

        setTitle("Digital Library Management System (Premium Edition)");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_LIGHT);

        // تحسين مظهر التبويبات (Tabs)
        UIManager.put("TabbedPane.selected", CARD_BG);
        UIManager.put("TabbedPane.contentAreaColor", CARD_BG);
        UIManager.put("TabbedPane.borderColor", new Color(230, 235, 240));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setForeground(PRIMARY_COLOR);

        tabbedPane.addTab("  Book Inventory (AVL)  ", createBooksPanel());
        tabbedPane.addTab("  Circulation (Borrow / Return)  ", createOperationsPanel());
        tabbedPane.addTab("  Management Reports  ", createReportsPanel());

        add(tabbedPane);
        refreshTable(null); // العرض الافتراضي لكل الكتب
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. شريط البحث العلوي المتطور (Search & Filter Section)
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(CARD_BG);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 235), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblSearch = createStyledLabel("Quick Search (ISBN):", new Font("Segoe UI", Font.BOLD, 13));
        JTextField txtSearchIsbn = new JTextField();
        txtSearchIsbn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JPanel searchActionPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        searchActionPanel.setBackground(CARD_BG);
        JButton btnSearch = createSystemButton("Find Book");
        JButton btnClearSearch = new JButton("Clear Filter");
        styleSecondaryButton(btnClearSearch);
        JButton btnBack = new JButton("→ العودة للرئيسية");

        btnBack.addActionListener(e -> {
            this.dispose(); // إغلاق نافذة المكتبة
            SwingUtilities.invokeLater(() -> new org.example.SplashScreen().setVisible(true));
        });


        searchActionPanel.add(btnSearch);
        searchActionPanel.add(btnClearSearch);

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearchIsbn, BorderLayout.CENTER);
        searchPanel.add(searchActionPanel, BorderLayout.EAST);

        // 2. تصميم جدول عرض الكتب
        String[] columns = {"ISBN Code", "Book Title", "Author", "Total Stock", "Available on Shelf"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // منع التعديل المباشر بالخلايا
        };
        booksTable = new JTable(tableModel);
        booksTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        booksTable.setRowHeight(28); // زيادة الارتفاع لتبدو الأسطر مريحة
        booksTable.setGridColor(new Color(235, 240, 245));
        booksTable.setSelectionBackground(new Color(240, 244, 248));
        booksTable.setSelectionForeground(PRIMARY_COLOR);
        
        JTableHeader header = booksTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 32));

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));

        // تجميع العناصر في القسم العلوي والمنتصف
        JPanel centerContainer = new JPanel(new BorderLayout(0, 15));
        centerContainer.setBackground(BG_LIGHT);
        centerContainer.add(searchPanel, BorderLayout.NORTH);
        centerContainer.add(scrollPane, BorderLayout.CENTER);
        panel.add(centerContainer, BorderLayout.CENTER);

        // 3. لوحة إدخال البيانات السفلية (Data Management Panel)
        JPanel inputCard = new JPanel(new BorderLayout());
        inputCard.setBackground(CARD_BG);
        inputCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 235), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 4, 15, 8));
        fieldsPanel.setBackground(CARD_BG);

        JTextField txtIsbn = new JTextField();
        JTextField txtTitle = new JTextField();
        JTextField txtAuthor = new JTextField();
        JTextField txtCopies = new JTextField();

        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);
        fieldsPanel.add(createStyledLabel("ISBN Code:", labelFont));
        fieldsPanel.add(createStyledLabel("Book Title:", labelFont));
        fieldsPanel.add(createStyledLabel("Author Name:", labelFont));
        fieldsPanel.add(createStyledLabel("Total Quantity:", labelFont));

        fieldsPanel.add(txtIsbn);
        fieldsPanel.add(txtTitle);
        fieldsPanel.add(txtAuthor);
        fieldsPanel.add(txtCopies);

        JPanel controlPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        controlPanel.setBackground(CARD_BG);
        controlPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JButton btnAdd = createSystemButton("Add New Book");
        JButton btnUpdate = createSystemButton("Update Stock");
        JButton btnDelete = new JButton("Purge Record");
        styleDangerButton(btnDelete);

        controlPanel.add(btnBack);
        controlPanel.add(Box.createVerticalStrut(15)); // مسافة فاصلة بين زر العودة وباقي القائمة

        controlPanel.add(btnAdd);
        controlPanel.add(btnUpdate);
        controlPanel.add(btnDelete);
        
        inputCard.add(fieldsPanel, BorderLayout.CENTER);
        inputCard.add(controlPanel, BorderLayout.SOUTH);

        panel.add(inputCard, BorderLayout.SOUTH);

        // --- ربط أحداث الأزرار (Action Listeners) ---
        
        // حدث البحث
        btnSearch.addActionListener(e -> {
            String searchStr = txtSearchIsbn.getText().trim();
            if (searchStr.isEmpty()) {
                refreshTable(null);
                return;
            }
            try {
                int isbn = Integer.parseInt(searchStr);
                Book foundBook = manager.getBookTree().search(isbn);
                if (foundBook != null) {
                    refreshTable(foundBook);
                } else {
                    JOptionPane.showMessageDialog(this, "No record matches the provided ISBN.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable(null);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric ISBN sequence.", "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnClearSearch.addActionListener(e -> {
            txtSearchIsbn.setText("");
            refreshTable(null);
        });

        btnAdd.addActionListener(e -> {
            try {
                int isbn = Integer.parseInt(txtIsbn.getText().trim());
                String title = txtTitle.getText().trim();
                String author = txtAuthor.getText().trim();
                int copies = Integer.parseInt(txtCopies.getText().trim());
                
                if (title.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Mandatory data fields are missing (Title / Author).", "Input Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (manager.addBook(isbn, title, author, copies)) {
                    JOptionPane.showMessageDialog(this, "Book data securely committed to the AVL Tree repository.");
                    refreshTable(null);
                    txtIsbn.setText(""); txtTitle.setText(""); txtAuthor.setText(""); txtCopies.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Operation Refused: ISBN primary key identity conflict.", "System Conflict", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Data type criteria violation: ISBN & Stock require absolute numbers.", "Validation Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUpdate.addActionListener(e -> {
            try {
                int isbn = Integer.parseInt(txtIsbn.getText().trim());
                int copies = Integer.parseInt(txtCopies.getText().trim());
                if (manager.updateCopies(isbn, copies)) {
                    JOptionPane.showMessageDialog(this, "Inventory balance updated successfully.");
                    refreshTable(null);
                } else {
                    JOptionPane.showMessageDialog(this, "Update Blocked: New limit collapses below currently borrowed active count.", "Constraint Violation", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please fill valid numerical parameters inside ISBN and Quantity fields.", "Validation Alert", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            try {
                int isbn = Integer.parseInt(txtIsbn.getText().trim());
                int confirm = JOptionPane.showConfirmDialog(this, "Are you absolutely sure you want to permanently delete this book?", "Confirm Action", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                if (manager.deleteBook(isbn)) {
                    JOptionPane.showMessageDialog(this, "Entity successfully wiped from the indexing system.");
                    refreshTable(null);
                    txtIsbn.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Target identifier record does not exist.", "Null Reference", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "A valid numerical ISBN target is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createOperationsPanel() {
        // تم استبدال الـ GridLayout بـ GridBagLayout لتوزيع هندسي متناسق ومنع تمدد الحقول بشكل بشع
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        Font sectionFont = new Font("Segoe UI", Font.BOLD, 14);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. إعادة هيكلة وتنسيق لوحة الاستعارة (Borrow Desk Card)
        JPanel borrowCard = new JPanel(new BorderLayout(10, 10));
        borrowCard.setBackground(CARD_BG);
        borrowCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220,225,230)), " Book Checkout Desk ", TitledBorder.LEFT, TitledBorder.TOP, sectionFont, PRIMARY_COLOR),
                new EmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel bGrid = new JPanel(new GridLayout(3, 4, 15, 12));
        bGrid.setBackground(CARD_BG);
        
        JTextField bIsbn = new JTextField();
        JTextField bName = new JTextField();
        JCheckBox chkGraduate = new JCheckBox("Graduating Student (Priority)");
        chkGraduate.setBackground(CARD_BG);
        chkGraduate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkGraduate.setForeground(ACCENT_COLOR);
        
        JTextField bDate = new JTextField("2026-06-28");
        JTextField rDate = new JTextField("2026-07-12");
        JButton btnBorrow = createSystemButton("Issue Loan Authorization");

        bGrid.add(createStyledLabel("Target Book ISBN:", bGrid.getFont())); bGrid.add(bIsbn);
        bGrid.add(createStyledLabel("Borrower Full Name:", bGrid.getFont())); bGrid.add(bName);
        bGrid.add(createStyledLabel("Effective Loan Date:", bGrid.getFont())); bGrid.add(bDate);
        bGrid.add(createStyledLabel("Expected Return:", bGrid.getFont())); bGrid.add(rDate);
        bGrid.add(chkGraduate); bGrid.add(new JLabel("")); bGrid.add(new JLabel("")); bGrid.add(btnBorrow);
        borrowCard.add(bGrid, BorderLayout.CENTER);

        // 2. إعادة هيكلة وتنسيق لوحة الإرجاع (Return Desk Card)
        JPanel returnCard = new JPanel(new BorderLayout(10, 10));
        returnCard.setBackground(CARD_BG);
        returnCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220,225,230)), " Book Returns Desk ", TitledBorder.LEFT, TitledBorder.TOP, sectionFont, PRIMARY_COLOR),
                new EmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel rGrid = new JPanel(new GridLayout(2, 4, 15, 12));
        rGrid.setBackground(CARD_BG);
        
        JTextField retIsbn = new JTextField();
        JTextField retName = new JTextField();
        JButton btnReturn = createSystemButton("Execute Return Processing");

        rGrid.add(createStyledLabel("Target Book ISBN:", rGrid.getFont())); rGrid.add(retIsbn);
        rGrid.add(createStyledLabel("Borrower Identity:", rGrid.getFont())); rGrid.add(retName);
        rGrid.add(new JLabel("")); rGrid.add(new JLabel("")); rGrid.add(new JLabel("")); rGrid.add(btnReturn);
        returnCard.add(rGrid, BorderLayout.CENTER);

        // إضافة الكروت إلى الـ Layout الرئيسي بالتساوي
        gbc.gridy = 0; gbc.weighty = 0.6; panel.add(borrowCard, gbc);
        gbc.gridy = 1; gbc.weighty = 0.4; panel.add(returnCard, gbc);

        // --- ربط أحداث العمليات الدائرية ---
        btnBorrow.addActionListener(e -> {
            try {
                int isbn = Integer.parseInt(bIsbn.getText().trim());
                String name = bName.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "System Alert: Identity tracking requires a valid name input.", "Missing Info", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String res = manager.borrowBook(isbn, name, chkGraduate.isSelected(), bDate.getText(), rDate.getText());
                JOptionPane.showMessageDialog(this, res, "Transaction Execution Monitor", JOptionPane.INFORMATION_MESSAGE);
                refreshTable(null);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Core Parsing Failure: Numerical ISBN formatting rule broken.", "Input Exception", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReturn.addActionListener(e -> {
            try {
                int isbn = Integer.parseInt(retIsbn.getText().trim());
                String name = retName.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please clarify the verification identity of the client.", "Identification Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String res = manager.returnBook(isbn, name);
                JOptionPane.showMessageDialog(this, res, "Transaction Execution Monitor", JOptionPane.INFORMATION_MESSAGE);
                refreshTable(null);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Operational failure triggered by faulty ISBN index value.", "Error Parameters", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Consolas", Font.BOLD, 14)); // خط برمجي عريض مناسب للتقارير والإحصائيات
        reportArea.setBackground(CARD_BG);
        reportArea.setForeground(TEXT_DARK);
        reportArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,225,230)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JButton btnGenerate = createSystemButton("Synchronize & Compile Live Infrastructure Analytics");
        btnGenerate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGenerate.setPreferredSize(new Dimension(200, 42));
        btnGenerate.addActionListener(e -> reportArea.setText(manager.generateReports()));
        panel.add(btnGenerate, BorderLayout.SOUTH);

        return panel;
    }

    // تحديث الجدول بشكل مرن (يدعم عرض شجرة الـ AVL كاملة أو فلترة سطر واحد)
    private void refreshTable(Book singleBook) {
        tableModel.setRowCount(0);
        if (singleBook != null) {
            tableModel.addRow(new Object[]{singleBook.isbn, singleBook.title, singleBook.author, singleBook.totalCopies, singleBook.availableCopies});
        } else {
            for (Book b : manager.getBookTree().getAllBooks()) {
                tableModel.addRow(new Object[]{b.isbn, b.title, b.author, b.totalCopies, b.availableCopies});
            }
        }
    }

    // دوال مساعدة موحدة للمحافظة على نمط تصميم ثابت (UI Design Utilities)
    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font.deriveFont(Font.BOLD));
        label.setForeground(PRIMARY_COLOR);
        return label;
    }

    private JButton createSystemButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(new Color(230, 235, 240));
        button.setForeground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 205, 210)));
    }

    private void styleDangerButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(new Color(186, 45, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }
}