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
import javax.swing.JOptionPane;

public class User {
    private int id;
    private String nama;
    private String email;
    private String password;
    
    // Constructor, getters, and setters
    public User(int id, String nama, String email, String password) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.password = password;
    }
    
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    
    // CRUD Operations
    public static boolean addUser(String nama, String email, String password) {
        String sql = "INSERT INTO users (nama, email, password) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nama);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }
    
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id_user"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting users: " + e.getMessage());
        }
        
        return users;
    }
    
    public static boolean updateUser(int id, String nama, String email, String password) {
        String sql = "UPDATE users SET nama = ?, email = ?, password = ? WHERE id_user = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nama);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean deleteUser(int id) {
        // Cek apakah user masih memiliki peminjaman aktif
    String checkSql = "SELECT COUNT(*) FROM peminjaman WHERE id_user = ? AND status = 'Dipinjam'";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
        
        checkStmt.setInt(1, id);
        ResultSet rs = checkStmt.executeQuery();
        
        if (rs.next() && rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(null,
                "User masih memiliki buku yang dipinjam!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Lanjutkan penghapusan jika valid
        String deleteSql = "DELETE FROM users WHERE id_user = ?";
        PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
        deleteStmt.setInt(1, id);
        return deleteStmt.executeUpdate() > 0;
        
    } catch (SQLException e) {
        System.err.println("Error deleting user: " + e.getMessage());
        return false;
    }
    }
    
    public static User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id_user = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id_user"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }
        
        return null;
    }
}
