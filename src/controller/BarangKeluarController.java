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

    public boolean tambahBarangKeluar(BarangKeluar barang) {
        String cekStokQuery = "SELECT jumlah FROM stok WHERE nama_barang = ?";
        String kurangiStokQuery = "UPDATE stok SET jumlah = jumlah - ? WHERE nama_barang = ?";
        String insertBarangKeluarQuery = "INSERT INTO barang_keluar (nama_barang, jumlah, tanggal, kategori) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement cekStokStmt = conn.prepareStatement(cekStokQuery);
                PreparedStatement kurangiStokStmt = conn.prepareStatement(kurangiStokQuery);
                PreparedStatement insertStmt = conn.prepareStatement(insertBarangKeluarQuery)) {

            // Cek stok terlebih dahulu
            cekStokStmt.setString(1, barang.getNamaBarang());
            ResultSet rs = cekStokStmt.executeQuery();
            if (rs.next()) {
                int stokTersedia = rs.getInt("jumlah");
                if (stokTersedia < barang.getJumlah()) {
                    System.out.println("Stok tidak mencukupi untuk barang: " + barang.getNamaBarang());
                    return false;
                }
            } else {
                System.out.println("Barang tidak ditemukan di stok: " + barang.getNamaBarang());
                return false;
            }

            // Kurangi stok
            kurangiStokStmt.setInt(1, barang.getJumlah());
            kurangiStokStmt.setString(2, barang.getNamaBarang());
            kurangiStokStmt.executeUpdate();

            // Masukkan data ke tabel barang_keluar
            insertStmt.setString(1, barang.getNamaBarang());
            insertStmt.setInt(2, barang.getJumlah());
            insertStmt.setString(3, barang.getTanggal());
            insertStmt.setString(4, barang.getKategori());
            int rows = insertStmt.executeUpdate();

            return rows > 0; // Return true jika data berhasil ditambahkan
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
    public void updateBarangKeluar(int id, String nama, String kategori, int jumlah, String tanggal) {
        String query = "UPDATE barang_keluar SET nama_barang = ?, kategori = ?, jumlah = ?, tanggal = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nama);
            stmt.setString(2, kategori);
            stmt.setInt(3, jumlah);
            stmt.setString(4, tanggal);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk mendapatkan daftar barang keluar dari database
    public List<BarangKeluar> getDaftarBarangKeluar() {
        List<BarangKeluar> daftarBarang = new ArrayList<>();
        String query = "SELECT * FROM barang_keluar";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama_barang");
                String kategori = rs.getString("kategori");
                int jumlah = rs.getInt("jumlah");
                String tanggal = rs.getString("tanggal");
                daftarBarang.add(new BarangKeluar(id, nama, kategori, jumlah, tanggal));
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
                    String kategori = rs.getString("kategori"); // Tambahkan ini
                    int jumlah = rs.getInt("jumlah");
                    String tanggal = rs.getString("tanggal");

                    // Sesuaikan konstruktor dengan field kategori
                    return new BarangKeluar(id, nama, kategori, jumlah, tanggal);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
