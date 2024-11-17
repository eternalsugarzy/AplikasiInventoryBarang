package model;

public class BarangKeluar {

    private int id;
    private String namaBarang;
    private String kategori;
    private int jumlah;
    private String tanggal;

    public BarangKeluar(int id, String namaBarang, String kategori, int jumlah, String tanggal) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public String getKategori() {
        return kategori;
    }

    public int getJumlah() {
        return jumlah;
    }

    public String getTanggal() {
        return tanggal;
    }
}
