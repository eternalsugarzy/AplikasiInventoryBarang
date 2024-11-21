package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.MessageDigest;

public class LoginController {

    public static boolean validateLogin(String username, String password) {
    boolean isValid = false;

    try {
        // Koneksi ke database
        Connection conn = DatabaseConnection.getConnection();

        // Query untuk validasi login
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password);

        // Debug untuk memeriksa input
        System.out.println("DEBUG: Username - " + username);
        System.out.println("DEBUG: Password - " + password);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            isValid = true;
        }

        // Tutup koneksi
        rs.close();
        stmt.close();
        conn.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

    return isValid;
}

    
   
}
