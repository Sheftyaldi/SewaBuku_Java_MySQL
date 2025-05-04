/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.gui;
// PeminjamanPanel.java

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import com.mycompany.mavenproject1.Model.Book;
import com.mycompany.mavenproject1.Model.User;
import com.mycompany.mavenproject1.Model.Borrow;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;

public class PeminjamanPanel extends JPanel implements MainFrame.Refreshable {
    private JComboBox<String> userComboBox; // Diubah ke String
    private JComboBox<String> bookComboBox; // Diubah ke String
    private JTable borrowTable;

    public PeminjamanPanel() {
        setLayout(new BorderLayout());
        initComponents();
        loadBorrowData();
    }

    private void initComponents() {
        // Kode layout yang sudah diperbaiki (nomor 2)
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ComboBox User (nomor 1)
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("User:"), gbc);

        gbc.gridx = 1;
        userComboBox = new JComboBox<>();
        for (User user : User.getAllUsers()) {
            userComboBox.addItem(user.getNama());
            userComboBox.putClientProperty(user.getNama(), user.getId());
        }
        inputPanel.add(userComboBox, gbc);

        // ComboBox Buku (nomor 1)
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Buku:"), gbc);

        gbc.gridx = 1;
        bookComboBox = new JComboBox<>();
        for (Book book : Book.getAllBooks()) {
            bookComboBox.addItem(book.getJudul());
            bookComboBox.putClientProperty(book.getJudul(), book.getId());
        }
        inputPanel.add(bookComboBox, gbc);

        // Tombol
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton borrowButton = new JButton("Pinjam Buku");
        borrowButton.addActionListener(e -> pinjamBuku());
        inputPanel.add(borrowButton, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Tabel
        borrowTable = new JTable(new DefaultTableModel(
            new Object[]{"ID", "User", "Buku", "Tanggal Pinjam", "Tanggal Kembali", "Status"}, 0
        ));
        add(new JScrollPane(borrowTable), BorderLayout.CENTER);
    }
    
        @Override
    public void refreshData() {
        loadBorrowData();
        refreshComboBoxes();
    }
    
    private void refreshComboBoxes() {
        userComboBox.removeAllItems();
        for (User user : User.getAllUsers()) {
            userComboBox.addItem(user.getNama());
            userComboBox.putClientProperty(user.getNama(), user.getId());
        }
        
        bookComboBox.removeAllItems();
        for (Book book : Book.getAllBooks()) {
            if ("Tersedia".equals(book.getStatus())) {
                bookComboBox.addItem(book.getJudul());
                bookComboBox.putClientProperty(book.getJudul(), book.getId());
            }
        }
    }
    


    private void pinjamBuku() {
    String userName = (String) userComboBox.getSelectedItem();
    String bookTitle = (String) bookComboBox.getSelectedItem();
    
    if (userName == null || bookTitle == null) {
        JOptionPane.showMessageDialog(this, "Pilih user dan buku!");
        return;
    }

    int userId = (int) userComboBox.getClientProperty(userName);
    int bookId = (int) bookComboBox.getClientProperty(bookTitle);

    // Cek status buku
    Book book = Book.getBookById(bookId);
    if (book != null && "Dipinjam".equals(book.getStatus())) {
        JOptionPane.showMessageDialog(this, 
            "Buku sedang dipinjam dan tidak tersedia!",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    Date now = new Date();
    if (Borrow.borrowBook(userId, bookId, now)) {
        JOptionPane.showMessageDialog(this, "Buku berhasil dipinjam!");
        
        // Refresh semua panel melalui MainFrame
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) {
                mainFrame.refreshAllPanels();
            }
    }
    }

    private void loadBorrowData() {
        DefaultTableModel model = (DefaultTableModel) borrowTable.getModel();
        model.setRowCount(0);
        
        for (Borrow borrow : Borrow.getAllBorrows()) {
            User user = User.getUserById(borrow.getIdUser());
            Book book = Book.getBookById(borrow.getIdBuku());

            model.addRow(new Object[]{
                borrow.getId(),
                (user != null) ? user.getNama() : "Unknown",
                (book != null) ? book.getJudul() : "Unknown",
                new SimpleDateFormat("dd-MM-yyyy").format(borrow.getTanggalPinjam()),
                new SimpleDateFormat("dd-MM-yyyy").format(borrow.getTanggalKembali()),
                borrow.getStatus()
            });
        }
    }
    
    private void refreshComboBoxes() {
    // Refresh user combo box
    userComboBox.removeAllItems();
    for (User user : User.getAllUsers()) {
        userComboBox.addItem(user.getNama());
        userComboBox.putClientProperty(user.getNama(), user.getId());
    }
    
    // Refresh book combo box
    bookComboBox.removeAllItems();
    for (Book book : Book.getAllBooks()) {
        if ("Tersedia".equals(book.getStatus())) {  // Hanya tampilkan buku yang tersedia
            bookComboBox.addItem(book.getJudul());
            bookComboBox.putClientProperty(book.getJudul(), book.getId());
        }
    }
}
}