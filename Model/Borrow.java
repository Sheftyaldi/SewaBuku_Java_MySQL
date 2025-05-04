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
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;

public class Borrow {
//    
     public static boolean borrowBook(int idUser, int idBuku, Date tanggalPinjam) {
        // Hitung tanggal kembali (7 hari setelah pinjam)
        Calendar cal = Calendar.getInstance();
        cal.setTime(tanggalPinjam);
        cal.add(Calendar.DATE, 7);
        Date tanggalKembali = cal.getTime();

        String sql = "INSERT INTO peminjaman (id_user, id_buku, tanggal_pinjam, tanggal_kembali, status) VALUES (?, ?, ?, ?, ?)";
        
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUser);
            pstmt.setInt(2, idBuku);
            pstmt.setTimestamp(3, new java.sql.Timestamp(tanggalPinjam.getTime()));
            pstmt.setTimestamp(4, new java.sql.Timestamp(tanggalKembali.getTime()));
            pstmt.setString(5, "Dipinjam");
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
        // Update status buku menjadi "Dipinjam"
        Book.updateBookStatus(idBuku, "Dipinjam");
        
        // Catat transaksi
        Transaction.recordTransaction("Peminjaman", idBuku, idUser);
        return true;
    }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Gagal meminjam buku: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
//    

    public static List<Borrow> getActiveBorrows() {
    List<Borrow> borrows = new ArrayList<>();
    // Gunakan alias yang konsisten dengan struktur database Anda
    // String sql = "SELECT peminjaman.*, users.nama, buku.judul FROM peminjaman JOIN users ON peminjaman.id_user = users.id_user JOIN buku ON peminjaman.id_buku = buku.id_buku WHERE peminjaman.status = 'Dipinjam';";
    String sql = "SELECT peminjaman.*, users.nama, buku.judul FROM peminjaman " +
                 "JOIN users ON peminjaman.id_user = users.id_user " +
                 "JOIN buku ON peminjaman.id_buku = buku.id_buku " +
                 "WHERE peminjaman.status = 'Dipinjam'";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            Borrow borrow = new Borrow(
                rs.getInt("id"),
                rs.getInt("id_user"),
                rs.getInt("id_buku"),
                rs.getDate("tanggal_pinjam"),
                rs.getDate("tanggal_kembali"),
                rs.getString("status"),
                rs.getString("nama"),  // userName
                rs.getString("judul")  // bookTitle
            );
            borrows.add(borrow);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, 
            "Error mengambil data peminjaman: " + e.getMessage(),
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    return borrows;
}
    
    
// Tambahkan method helper di kelas Borrow
public String getFormattedTanggalPinjam() {
    return new SimpleDateFormat("dd-MM-yyyy").format(tanggalPinjam);
}

public String getFormattedTanggalKembali() {
    return new SimpleDateFormat("dd-MM-yyyy").format(tanggalKembali);
}

    private final int id;
    private final int idUser;
    private final int idBuku;
    private final Date tanggalPinjam;
    private final Date tanggalKembali;
    private final String status;
    
     // Constructor baru dengan semua parameter
    public Borrow(int id, int idUser, int idBuku, Date tanggalPinjam,
                 Date tanggalKembali, String status, String userName, String bookTitle) {
        this.id = id;
        this.idUser = idUser;
        this.idBuku = idBuku;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
        this.status = status;
        this.userName = userName;
        this.bookTitle = bookTitle;
    }
    
    // Constructor, getters, and setters
//    public Borrow(int id, int idUser, int idBuku, Date tanggalPinjam, Date tanggalKembali, String status) {
//        this.id = id;
//        this.idUser = idUser;
//        this.idBuku = idBuku;
//        this.tanggalPinjam = tanggalPinjam;
//        this.tanggalKembali = tanggalKembali;
//        this.status = status;
//    }
    
     // Di constructor, tambahkan parameter opsional:
    public Borrow(int id, int idUser, int idBuku, Date tanggalPinjam, 
                 Date tanggalKembali, String status) {
        this(id, idUser, idBuku, tanggalPinjam, tanggalKembali, status, null, null);
    }

    
    public int getId() { return id; }
    public int getIdUser() { return idUser; }
    public int getIdBuku() { return idBuku; }
    public Date getTanggalPinjam() { return tanggalPinjam; }
    public Date getTanggalKembali() { return tanggalKembali; }
    public String getStatus() { return status; }
    
    // CRUD Operations
   public static boolean borrowBook(int idUser, int idBuku, Date tanggalPinjam, Date tanggalKembali) {
    String sql = "INSERT INTO peminjaman (id_user, id_buku, tanggal_pinjam, tanggal_kembali, status) VALUES (?, ?, ?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        // 1. Catat peminjaman
        pstmt.setInt(1, idUser);
        pstmt.setInt(2, idBuku);
        pstmt.setString(3, sdf.format(tanggalPinjam));
        pstmt.setString(4, sdf.format(tanggalKembali));
        pstmt.setString(5, "Dipinjam");
        pstmt.executeUpdate();
        
        // 2. Catat transaksi (tambahkan ini!)
        Transaction.recordTransaction("Peminjaman", idBuku, idUser);
        
        return true;
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, 
            "Gagal meminjam buku: " + e.getMessage(),
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
}
    
    public static List<Borrow> getAllBorrows() {
        List<Borrow> borrows = new ArrayList<>();
        String sql = "SELECT * FROM peminjaman";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                borrows.add(new Borrow(
                    rs.getInt("id"),
                    rs.getInt("id_user"),
                    rs.getInt("id_buku"),
                    rs.getDate("tanggal_pinjam"),
                    rs.getDate("tanggal_kembali"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
            "Error mengambil data peminjaman: " + e.getMessage(),
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        }
        
        return borrows;
    }
    
    public static boolean updateBorrowStatus(int id, String status) {
        String sql = "UPDATE peminjaman SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating borrow status: " + e.getMessage());
            return false;
        }
    }
    
    public static Borrow getBorrowById(int id) {
        String sql = "SELECT * FROM peminjaman WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Borrow(
                    rs.getInt("id"),
                    rs.getInt("id_user"),
                    rs.getInt("id_buku"),
                    rs.getDate("tanggal_pinjam"),
                    rs.getDate("tanggal_kembali"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting borrow: " + e.getMessage());
        }
        
        return null;
    }
    
    public static List<Borrow> getBorrowsByUserId(int idUser) {
        List<Borrow> borrows = new ArrayList<>();
        String sql = "SELECT * FROM peminjaman WHERE id_user = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUser);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                borrows.add(new Borrow(
                    rs.getInt("id"),
                    rs.getInt("id_user"),
                    rs.getInt("id_buku"),
                    rs.getDate("tanggal_pinjam"),
                    rs.getDate("tanggal_kembali"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting user borrows: " + e.getMessage());
        }
        
        return borrows;
    }
    
    // Tambahkan field baru
    private String userName;
    private String bookTitle;

   
    
          // Implementasi method yang benar
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    // Getters untuk field baru
    public String getUserName() {
        return userName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

}