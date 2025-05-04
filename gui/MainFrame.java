/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.gui;

import com.mycompany.mavenproject1.Model.Book;
import com.mycompany.mavenproject1.Model.Borrow;
import com.mycompany.mavenproject1.Model.User;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private List<Refreshable> refreshablePanels = new ArrayList<>();
    
    public interface Refreshable {
        void refreshData();
    }
    
    public void registerRefreshablePanel(Refreshable panel) {
        refreshablePanels.add(panel);
    }
    
    public void refreshAllPanels() {
        for (Refreshable panel : refreshablePanels) {
            panel.refreshData();
        }
    }

    public MainFrame() {
        setTitle("Sistem Sewa Buku");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();

        BookPanel bookPanel = new BookPanel();
        UserPanel userPanel = new UserPanel();
        PeminjamanPanel peminjamanPanel = new PeminjamanPanel();
        PengembalianPanel pengembalianPanel = new PengembalianPanel();
        TransactionPanel transactionPanel = new TransactionPanel();
        
        registerRefreshablePanel(bookPanel);
        registerRefreshablePanel(userPanel);
        registerRefreshablePanel(peminjamanPanel);
        registerRefreshablePanel(pengembalianPanel);
        registerRefreshablePanel(transactionPanel);
        
        // Tambahkan panel-panel
        
        tabbedPane.addTab("Buku", new BookPanel());
        tabbedPane.addTab("User", new UserPanel());
        tabbedPane.addTab("Peminjaman", new PeminjamanPanel());
        tabbedPane.addTab("Pengembalian", new PengembalianPanel());
        tabbedPane.addTab("Transaksi", new TransactionPanel());

//        add(tabbedPane, BorderLayout.CENTER);
    }
    
    // Di constructor MainFrame(), tambahkan:
private void initSampleData() {
    // Contoh data user dan buku
    User.addUser("Admin", "admin@email.com", "password123");
    Book.addBook("Buku Contoh", "Penulis A", 2023);
    
    // Contoh peminjaman
    Date now = new Date();
    Date returnDate = new Date(now.getTime() + (7 * 24 * 60 * 60 * 1000)); // +7 hari
    Borrow.borrowBook(1, 1, now, returnDate);
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}