package model;

public class BarangMasuk {

    private int id;
    private String namaBarang;
    private String kategori;
    private int jumlah;
    private double harga;
    private String tanggal;

    // Konstruktor lengkap
    public BarangMasuk(int id, String namaBarang, String kategori, int jumlah, double harga, String tanggal) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.jumlah = jumlah;
        this.harga = harga;
        this.tanggal = tanggal;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
