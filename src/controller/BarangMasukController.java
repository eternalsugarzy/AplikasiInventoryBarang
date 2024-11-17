package controller;

import model.BarangMasuk;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangMasukController {

    private Connection connection;

    public BarangMasukController() {
        connection = DatabaseConnection.getConnection();
    }

    // Fungsi untuk menambahkan barang masuk
    public void tambahBarangMasuk(BarangMasuk barang) {
        String query = "INSERT INTO barang_masuk (nama_barang, jumlah, harga, tanggal) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, barang.getNamaBarang());
            stmt.setInt(2, barang.getJumlah());
            stmt.setDouble(3, barang.getHarga());
            stmt.setString(4, barang.getTanggal());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk menghapus barang masuk berdasarkan ID
    public void hapusBarangMasuk(int id) {
        String query = "DELETE FROM barang_masuk WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk memperbarui data barang masuk
    public void updateBarangMasuk(int id, String nama, int jumlah, double harga, String tanggal) {
        String query = "UPDATE barang_masuk SET nama_barang = ?, jumlah = ?, harga = ?, tanggal = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nama);
            stmt.setInt(2, jumlah);
            stmt.setDouble(3, harga);
            stmt.setString(4, tanggal);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk mendapatkan daftar barang masuk dari database
    public List<BarangMasuk> getDaftarBarangMasuk() {
        List<BarangMasuk> daftarBarang = new ArrayList<>();
        String query = "SELECT * FROM barang_masuk";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama_barang");
                int jumlah = rs.getInt("jumlah");
                double harga = rs.getDouble("harga");
                String tanggal = rs.getString("tanggal");
                daftarBarang.add(new BarangMasuk(id, nama, jumlah, harga, tanggal));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return daftarBarang;
    }

    // Tambahkan method ini di BarangMasukController
    public BarangMasuk getBarangByNama(String namaBarang) {
        BarangMasuk barang = null;
        String query = "SELECT * FROM barang_masuk WHERE nama_barang = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, namaBarang);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama_barang");
                int jumlah = rs.getInt("jumlah");
                double harga = rs.getDouble("harga");
                String tanggal = rs.getString("tanggal");
                barang = new BarangMasuk(id, nama, jumlah, harga, tanggal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return barang;
    }

}
