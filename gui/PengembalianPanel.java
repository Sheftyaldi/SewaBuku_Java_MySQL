/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.gui;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import com.mycompany.mavenproject1.Model.Borrow;
import com.mycompany.mavenproject1.Model.Return;

public class PengembalianPanel extends JPanel {
    private JTable borrowTable;
    private DefaultTableModel tableModel;

    public PengembalianPanel() {
        setLayout(new BorderLayout());
        initComponents();
        loadActiveBorrows();
    }

    private void initComponents() {
        // Tabel peminjaman aktif
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "User", "Buku", "Tanggal Pinjam", "Tanggal Kembali"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable table
            }
        };
        
        borrowTable = new JTable(tableModel);
        borrowTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel();
        JButton returnButton = new JButton("Kembalikan Buku");
        returnButton.addActionListener(this::handleReturn);
        buttonPanel.add(returnButton);
        
        add(new JScrollPane(borrowTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadActiveBorrows() {
        tableModel.setRowCount(0); // Clear existing data
        
        for (Borrow borrow : Borrow.getActiveBorrows()) {
            tableModel.addRow(new Object[]{
                borrow.getId(),
                borrow.getUserName(),
                borrow.getBookTitle(),
                borrow.getFormattedTanggalPinjam(),
                borrow.getFormattedTanggalKembali()
            });
        }
    }

    private void handleReturn(ActionEvent e) {
        int selectedRow = borrowTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Pilih peminjaman yang akan dikembalikan!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int borrowId = (int) borrowTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Konfirmasi pengembalian buku?", 
            "Konfirmasi", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (Return.returnBook(borrowId)) {
                JOptionPane.showMessageDialog(this, "Buku berhasil dikembalikan!");
                loadActiveBorrows(); // Refresh tabel
            }
        }
    }
}
