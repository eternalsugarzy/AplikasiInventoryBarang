package model;

public class BarangMasuk {

    private int id;
    private String namaBarang;
    private int jumlah;
    private double harga;
    private String tanggal;

    public BarangMasuk(int id, String namaBarang, int jumlah, double harga, String tanggal) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.jumlah = jumlah;
        this.harga = harga;
        this.tanggal = tanggal;
    }

    public int getId() {
        return id;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public int getJumlah() {
        return jumlah;
    }

    public double getHarga() {
        return harga;
    }

    public String getTanggal() {
        return tanggal;
    }
}
