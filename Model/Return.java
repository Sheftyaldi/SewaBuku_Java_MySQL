/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1.Model;
import java.sql.*;
import javax.swing.JOptionPane;

public class Return {
    public static boolean returnBook(int borrowId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Mulai transaksi
            
            // 1. Dapatkan id buku dari peminjaman
            String getBookSql = "SELECT id_buku FROM peminjaman WHERE id = ?";
            PreparedStatement getBookStmt = conn.prepareStatement(getBookSql);
            getBookStmt.setInt(1, borrowId);
            ResultSet rs = getBookStmt.executeQuery();
            
            if (!rs.next()) {
                throw new SQLException("Data peminjaman tidak ditemukan");
            }
            int idBuku = rs.getInt("id_buku");
            
            // 2. Update status peminjaman
            String updateBorrowSql = "UPDATE peminjaman SET status = 'Dikembalikan' WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateBorrowSql);
            updateStmt.setInt(1, borrowId);
            updateStmt.executeUpdate();
            
            // 3. Update status buku menjadi "Tersedia"
            Book.updateBookStatus(idBuku, "Tersedia");
            
            // 4. Catat transaksi pengembalian
            Transaction.recordTransaction("Pengembalian", idBuku, 
                getUserIdFromBorrow(conn, borrowId));
            
            conn.commit(); // Commit transaksi
            return true;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Gagal mengembalikan buku: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private static int getUserIdFromBorrow(Connection conn, int borrowId) throws SQLException {
        String sql = "SELECT id_user FROM peminjaman WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, borrowId);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt("id_user") : -1;
    }
}