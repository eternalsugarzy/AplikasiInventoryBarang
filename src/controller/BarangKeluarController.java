package controller;

import model.BarangKeluar;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangKeluarController {
    private Connection connection;

    public BarangKeluarController() {
        connection = DatabaseConnection.getConnection();
    }

    public void tambahBarangKeluar(BarangKeluar barang) {
        String query = "INSERT INTO barang_keluar (nama_barang, jumlah, tanggal) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, barang.getNamaBarang());
            stmt.setInt(2, barang.getJumlah());
            stmt.setString(3, barang.getTanggal());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Fungsi untuk menghapus barang keluar berdasarkan ID
    public void hapusBarangKeluar(int id) {
        String query = "DELETE FROM barang_keluar WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Fungsi untuk memperbarui data barang keluar
    public void updateBarangKeluar(int id, String nama, int jumlah, String tanggal) {
        String query = "UPDATE barang_keluar SET nama_barang = ?, jumlah = ?, tanggal = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nama);
            stmt.setInt(2, jumlah);
            stmt.setString(3, tanggal);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Fungsi untuk mendapatkan daftar barang keluar dari database
    public List<BarangKeluar> getDaftarBarangKeluar() {
        List<BarangKeluar> daftarBarang = new ArrayList<>();
        String query = "SELECT * FROM barang_keluar";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama_barang");
                int jumlah = rs.getInt("jumlah");
                String tanggal = rs.getString("tanggal");
                daftarBarang.add(new BarangKeluar(id, nama, jumlah, tanggal));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return daftarBarang;
    }
    
    // Fungsi untuk mendapatkan data barang keluar berdasarkan ID
    public BarangKeluar getBarangKeluarById(int id) {
        String query = "SELECT * FROM barang_keluar WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nama = rs.getString("nama_barang");
                    int jumlah = rs.getInt("jumlah");
                    String tanggal = rs.getString("tanggal");
                    return new BarangKeluar(id, nama, jumlah, tanggal);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
