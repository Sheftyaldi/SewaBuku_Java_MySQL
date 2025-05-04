/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.gui;

/**
 *
 * @author aldi
 */

import com.mycompany.mavenproject1.Model.Book;
import javax.swing.*;
import java.awt.*;
import com.mycompany.mavenproject1.Model.Transaction;
import com.mycompany.mavenproject1.Model.User;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class TransactionPanel extends JPanel {
   private JTable transactionTable;

    public TransactionPanel() {
        setLayout(new BorderLayout());
        initComponents();
        loadTransactionData();
    }
     private void initComponents() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Jenis");
        model.addColumn("Buku ID");
        model.addColumn("User ID");
        model.addColumn("Tanggal");

        transactionTable = new JTable(model);
        add(new JScrollPane(transactionTable), BorderLayout.CENTER);
    }

    private void loadTransactionData() {
    DefaultTableModel model = (DefaultTableModel) transactionTable.getModel();
    model.setRowCount(0); // Kosongkan tabel
    
    List<Transaction> transactions = Transaction.getAllTransactions();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    
    for (Transaction t : transactions) {
        // Ambil detail buku dan user
        Book book = Book.getBookById(t.getIdBuku());
        User user = User.getUserById(t.getIdUser());
        
        model.addRow(new Object[]{
            t.getIdTransaksi(),
            t.getJenis(),
            (book != null) ? book.getJudul() : "Buku tidak ditemukan",
            (user != null) ? user.getNama() : "User tidak ditemukan",
            sdf.format(t.getTanggal())
        });
    }
}
}
