/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import com.mycompany.mavenproject1.Model.User;

public class UserPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField namaField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton addButton, editButton, deleteButton;
    private int selectedUserId = -1;

    public UserPanel() {
        setLayout(new BorderLayout());
        initComponents();
        loadUserData();
    }

    private void initComponents() {
        // Input Form
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nama Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Nama:"), gbc);
        
        gbc.gridx = 1;
        namaField = new JTextField(20);
        inputPanel.add(namaField, gbc);

        // Email Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(20);
        inputPanel.add(emailField, gbc);

        // Password Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        inputPanel.add(passwordField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        addButton = new JButton("Tambah");
        addButton.addActionListener(this::handleAddUser);
        
        editButton = new JButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(this::handleEditUser);
        
        deleteButton = new JButton("Hapus");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(this::handleDeleteUser);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nama", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable table
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedUserId = (int) userTable.getValueAt(selectedRow, 0);
                    namaField.setText(userTable.getValueAt(selectedRow, 1).toString());
                    emailField.setText(userTable.getValueAt(selectedRow, 2).toString());
                    passwordField.setText("");
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    addButton.setEnabled(false);
                }
            }
        });
        
        add(new JScrollPane(userTable), BorderLayout.CENTER);
    }

    private void loadUserData() {
        tableModel.setRowCount(0);
        for (User user : User.getAllUsers()) {
            tableModel.addRow(new Object[]{
                user.getId(),
                user.getNama(),
                user.getEmail()
            });
        }
        clearForm();
    }

    private void clearForm() {
        namaField.setText("");
        emailField.setText("");
        passwordField.setText("");
        selectedUserId = -1;
        addButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        userTable.clearSelection();
    }

    private void handleAddUser(ActionEvent e) {
        String nama = namaField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (User.addUser(nama, email, password)) {
            JOptionPane.showMessageDialog(this, "User berhasil ditambahkan");
            loadUserData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan user", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEditUser(ActionEvent e) {
        String nama = namaField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user yang akan diedit!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (User.updateUser(selectedUserId, nama, email, password)) {
            JOptionPane.showMessageDialog(this, "User berhasil diupdate");
            loadUserData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate user", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteUser(ActionEvent e) {
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user yang akan dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Hapus user ini?", 
            "Konfirmasi", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (User.deleteUser(selectedUserId)) {
                JOptionPane.showMessageDialog(this, "User berhasil dihapus");
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}