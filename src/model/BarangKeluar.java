package model;

public class BarangKeluar {

    private int id;
    private String namaBarang;
    private int jumlah;
    private String tanggal;

    public BarangKeluar(int id, String namaBarang, int jumlah, String tanggal) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.jumlah = jumlah;
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

    public String getTanggal() {
        return tanggal;
    }
}
