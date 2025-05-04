/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.gui;

/**
 *
 * @author aldi
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.mycompany.mavenproject1.Model.Book;
import java.util.List;

public class BookPanel extends JPanel implements MainFrame.Refreshable {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    
    @Override
    public void refreshData() {
        loadBookData();
    }

    public BookPanel() {
        setLayout(new BorderLayout());
        initComponents();
        loadBookData();
    }

    private void initComponents() {
        // Model tabel
        tableModel = new DefaultTableModel();
        tableModel.addColumn("id_buku");
        tableModel.addColumn("judul");
        tableModel.addColumn("penulis");
        tableModel.addColumn("tahun_terbit");

        // Tabel
        bookTable = new JTable(tableModel);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Tambah");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Hapus");

        addButton.addActionListener(e -> showAddBookDialog());
        editButton.addActionListener(e -> showEditBookDialog());
        deleteButton.addActionListener(e -> deleteBook());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadBookData() {
        tableModel.setRowCount(0); // Clear existing data
        List<Book> books = Book.getAllBooks();
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                book.getId(),
                book.getJudul(),
                book.getPenulis(),
                book.getTahunTerbit()
            });
        }
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Tambah Buku Baru");
        dialog.setSize(400, 300);
        dialog.setModal(true);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        
        JTextField judulField = new JTextField();
        JTextField penulisField = new JTextField();
        JTextField tahunField = new JTextField();

        panel.add(new JLabel("Judul:"));
        panel.add(judulField);
        panel.add(new JLabel("Penulis:"));
        panel.add(penulisField);
        panel.add(new JLabel("Tahun Terbit:"));
        panel.add(tahunField);

        JButton saveButton = new JButton("Simpan");
        saveButton.addActionListener(e -> {
            String judul = judulField.getText();
            String penulis = penulisField.getText();
            int tahun = Integer.parseInt(tahunField.getText());

            Book.addBook(judul, penulis, tahun);
            loadBookData();
            dialog.dispose();
        });

        panel.add(saveButton);
        dialog.add(panel);
        dialog.setVisible(true);
        
         saveButton.addActionListener(e -> {
            String judul = judulField.getText();
            String penulis = penulisField.getText();
            int tahun = Integer.parseInt(tahunField.getText());

            if (Book.addBook(judul, penulis, tahun)) {
                // Dapatkan referensi ke MainFrame
                MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(BookPanel.this);
                if (mainFrame != null) {
                    mainFrame.refreshAllPanels();
                }
                dialog.dispose();
            }
        });
    }
    }

    // Implementasi edit dan delete serupa
    // ...

    private void showEditBookDialog() {
         int selectedRow = bookTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih buku yang akan diubah!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    int id = (int) bookTable.getValueAt(selectedRow, 0);
    Book book = Book.getBookById(id); // Ambil data dari database

    JDialog dialog = new JDialog();
    dialog.setTitle("Edit Buku");
    dialog.setLayout(new GridLayout(4, 2));

    JTextField judulField = new JTextField(book.getJudul());
    JTextField penulisField = new JTextField(book.getPenulis());
    JTextField tahunField = new JTextField(String.valueOf(book.getTahunTerbit()));

    dialog.add(new JLabel("Judul:"));
    dialog.add(judulField);
    dialog.add(new JLabel("Penulis:"));
    dialog.add(penulisField);
    dialog.add(new JLabel("Tahun Terbit:"));
    dialog.add(tahunField);

    JButton saveButton = new JButton("Simpan");
    saveButton.addActionListener(e -> {
             boolean updateBookStatus = Book.updateBookStatus(id,
                     judulField.getText());
        loadBookData(); // Refresh tabel
        dialog.dispose();
    });

    dialog.add(saveButton);
    dialog.pack();
    dialog.setVisible(true);
    }

    private void deleteBook() {
         int selectedRow = bookTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih buku yang akan dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    int id = (int) bookTable.getValueAt(selectedRow, 0);
    int confirm = JOptionPane.showConfirmDialog(
        this, 
        "Hapus buku ini?", 
        "Konfirmasi", 
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        Book.deleteBook(id);
        loadBookData(); // Refresh tabel
    }
    }
}