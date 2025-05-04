/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Model;

import com.mycompany.mavenproject1.Model.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Transaction {
    private int idTransaksi;
    private String jenis;
    private int idBuku;
    private int idUser;
    private Date tanggal;
    
    // Constructor, getters, and setters
    public Transaction(int idTransaksi, String jenis, int idBuku, int idUser, Date tanggal) {
        this.idTransaksi = idTransaksi;
        this.jenis = jenis;
        this.idBuku = idBuku;
        this.idUser = idUser;
        this.tanggal = tanggal;
    }
    
    public int getIdTransaksi() { return idTransaksi; }
    public String getJenis() { return jenis; }
    public int getIdBuku() { return idBuku; }
    public int getIdUser() { return idUser; }
    public Date getTanggal() { return tanggal; }
    
    // Record a new transaction
    public static void recordTransaction(String jenis, int idBuku, int idUser) {
        String sql = "INSERT INTO tranksaksi (jenis, id_buku, id_user, tanggal) VALUES (?, ?, ?, NOW())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, jenis);
            pstmt.setInt(2, idBuku);
            pstmt.setInt(3, idUser);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error recording transaction: " + e.getMessage());
        }
    }
    
    // Get all transactions
    public static List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM tranksaksi";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getInt("id_transaksi"),
                    rs.getString("jenis"),
                    rs.getInt("id_buku"),
                    rs.getInt("id_user"),
                    rs.getTimestamp("tanggal")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    // Get transactions by user ID
    public static List<Transaction> getTransactionsByUserId(int idUser) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM tranksaksi WHERE id_user = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUser);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getInt("id_transaksi"),
                    rs.getString("jenis"),
                    rs.getInt("id_buku"),
                    rs.getInt("id_user"),
                    rs.getTimestamp("tanggal")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting user transactions: " + e.getMessage());
        }
        
        return transactions;
    }
}
