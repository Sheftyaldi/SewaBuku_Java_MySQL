/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Book {

    private static String status;
    private int id;
    private String judul;
    private String penulis;
    private int tahunTerbit;
    
    // Constructor, getters, and setters
    public Book(int id, String judul, String penulis, int tahunTerbit, String string2) {
        this.id = id;
        this.judul = judul;
        this.penulis = penulis;
        this.tahunTerbit = tahunTerbit;
    }
    
    public int getId() { return id; }
    public String getJudul() { return judul; }
    public String getPenulis() { return penulis; }
    public int getTahunTerbit() { return tahunTerbit; }
    public String getStatus() {return status; }
    
    // CRUD Operations
    public static boolean addBook(String judul, String penulis, int tahunTerbit) {
        String sql = "INSERT INTO buku (judul, penulis, tahun_terbit) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, judul);
            pstmt.setString(2, penulis);
            pstmt.setInt(3, tahunTerbit);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            return false;
        }
    }
    
    public static List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM buku";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id_buku"),
                    rs.getString("judul"),
                    rs.getString("penulis"),
                    rs.getInt("tahun_terbit"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
            "Error loading books: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return books;
    }
    
    public static boolean updateBookStatus(int id, String newStatus) {
        String sql = "UPDATE buku SET status = ? WHERE id_buku = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book status: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean deleteBook(int id) {
       // Cek apakah buku sedang dipinjam
    String checkSql = "SELECT COUNT(*) FROM peminjaman WHERE id_buku = ? AND status = 'Dipinjam'";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
        
        checkStmt.setInt(1, id);
        ResultSet rs = checkStmt.executeQuery();
        
        if (rs.next() && rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(null,
                "Buku masih dipinjam dan tidak bisa dihapus!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Lanjutkan penghapusan jika valid
        String deleteSql = "DELETE FROM buku WHERE id_buku = ?";
        PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
        deleteStmt.setInt(1, id);
        return deleteStmt.executeUpdate() > 0;
        
    } catch (SQLException e) {
        System.err.println("Error deleting book: " + e.getMessage());
        return false;
    }
    }
    
    public static Book getBookById(int id) {
        String sql = "SELECT * FROM buku WHERE id_buku = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Book(
                    rs.getInt("id_buku"),
                    rs.getString("judul"),
                    rs.getString("penulis"),
                    rs.getInt("tahun_terbit"), rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting book: " + e.getMessage());
        }
        
        return null;
    }
}