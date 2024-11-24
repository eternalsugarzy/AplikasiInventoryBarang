package view;

import controller.BarangMasukController;
import controller.BarangKeluarController;
import model.BarangMasuk;
import model.BarangKeluar;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class StokView extends javax.swing.JFrame {

    private BarangMasukController barangMasukController;
    private BarangKeluarController barangKeluarController;
    private DefaultTableModel model;

    /**
     * Creates new form StokView
     */
    public StokView() {
        initComponents();
        barangMasukController = new BarangMasukController();
        barangKeluarController = new BarangKeluarController();
        model = (DefaultTableModel) tblStok.getModel();
        loadData();

        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });

    }

    // Method untuk memuat data barang masuk dan keluar
    private void loadData() {
        model.setRowCount(0); // Membersihkan tabel sebelum memuat data baru

        // DecimalFormat untuk format harga
        DecimalFormat formatter = new DecimalFormat("#,###.00");

        // HashMap untuk menyimpan total jumlah barang berdasarkan nama
        Map<String, Integer> stokBarang = new HashMap<>();
        Map<String, String> kategoriBarang = new HashMap<>();
        Map<String, Double> hargaBarang = new HashMap<>();
        Map<String, String> tanggalBarang = new HashMap<>();
        Map<String, String> keteranganBarang = new HashMap<>();

        // Muat data barang masuk dan akumulasi jumlah
        List<BarangMasuk> daftarBarangMasuk = barangMasukController.getDaftarBarangMasuk();
        for (BarangMasuk barang : daftarBarangMasuk) {
            String namaBarang = barang.getNamaBarang();
            stokBarang.put(namaBarang, stokBarang.getOrDefault(namaBarang, 0) + barang.getJumlah());
            kategoriBarang.put(namaBarang, barang.getKategori());
            hargaBarang.put(namaBarang, barang.getHarga());
            tanggalBarang.put(namaBarang, barang.getTanggal());
            keteranganBarang.put(namaBarang, "Masuk");
        }

        // Muat data barang keluar dan kurangi jumlah
        List<BarangKeluar> daftarBarangKeluar = barangKeluarController.getDaftarBarangKeluar();
        for (BarangKeluar barang : daftarBarangKeluar) {
            String namaBarang = barang.getNamaBarang();
            stokBarang.put(namaBarang, stokBarang.getOrDefault(namaBarang, 0) - barang.getJumlah());
            tanggalBarang.put(namaBarang, barang.getTanggal());
            keteranganBarang.put(namaBarang, "Keluar");
        }

        // Tampilkan data di tabel
        for (String namaBarang : stokBarang.keySet()) {
            String formattedPrice = formatter.format(hargaBarang.getOrDefault(namaBarang, 0.0)); // Format harga
            model.addRow(new Object[]{
                namaBarang,
                kategoriBarang.getOrDefault(namaBarang, "-"), // Kategori
                stokBarang.get(namaBarang), // Jumlah total
                formattedPrice, // Harga dengan format
                tanggalBarang.getOrDefault(namaBarang, "-"), // Tanggal terakhir
                keteranganBarang.getOrDefault(namaBarang, "-") // Keterangan (Masuk/Keluar)
            });
        }
    }

    private void exportToTXT() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan File TXT");

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            // Tambahkan ekstensi ".txt" jika belum ada
            if (!filePath.endsWith(".txt")) {
                filePath += ".txt";
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                // Tulis header kolom
                writer.write("Nama Barang\tKategori\tJumlah\tHarga\tTanggal\tKeterangan");
                writer.newLine();

                // Iterasi melalui data di tabel dan tulis ke file
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);

                        if (j == 3 && value != null) { // Format kolom harga (indeks ke-3)
                            // Hapus koma pemisah ribuan menggunakan replaceAll
                            String harga = value.toString().replace(",", "");
                            writer.write(harga);
                        } else {
                            writer.write(value != null ? value.toString() : "");
                        }

                        if (j < model.getColumnCount() - 1) {
                            writer.write("\t"); // Pisahkan kolom dengan tab
                        }
                    }
                    writer.newLine(); // Pindah ke baris berikutnya
                }

                JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke: " + filePath);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Gagal mengekspor data: " + e.getMessage());
            }
        }
    }

    private void importFromTXT() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih File TXT untuk Diimpor");

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToImport = fileChooser.getSelectedFile();

            try (BufferedReader reader = new BufferedReader(new FileReader(fileToImport))) {
                String line;
                boolean isHeader = true; // Flag untuk melewati header

                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false; // Lewati baris pertama (header)
                        continue;
                    }

                    // Pecah baris berdasarkan tab '\t'
                    String[] data = line.split("\t");

                    if (data.length >= 6) { // Pastikan jumlah kolom sesuai
                        String namaBarang = data[0];
                        String kategori = data[1];
                        int jumlah = Integer.parseInt(data[2]);
                        double harga = Double.parseDouble(data[3]);
                        String tanggal = data[4];
                        String keterangan = data[5];

                        // Tambahkan data ke tabel stok
                        model.addRow(new Object[]{namaBarang, kategori, jumlah, harga, tanggal, keterangan});

                        // (Opsional) Tambahkan ke database stok
                        // stokController.tambahDataStok(new Stok(namaBarang, kategori, jumlah, harga, tanggal, keterangan));
                    }
                }
                JOptionPane.showMessageDialog(this, "Data berhasil diimpor dari: " + fileToImport.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Gagal membaca file: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Format data di file tidak valid: " + e.getMessage());
            }
        }
    }

    private void exportToPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan File PDF");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            // Tambahkan ekstensi ".pdf" jika tidak ada
            if (!filePath.endsWith(".pdf")) {
                filePath += ".pdf";
            }

            try {
                // Membuat dokumen PDF
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // Menambahkan judul
                document.add(new Paragraph("Laporan Stok Barang"));
                document.add(new Paragraph("Tanggal: " + java.time.LocalDate.now()));
                document.add(new Paragraph(" ")); // Spasi

                // Membuat tabel PDF
                PdfPTable table = new PdfPTable(model.getColumnCount());
                table.setWidthPercentage(100);

                // Menambahkan header kolom
                for (int i = 0; i < model.getColumnCount(); i++) {
                    table.addCell(new PdfPCell(new Phrase(model.getColumnName(i))));
                }

                // Menambahkan data baris dari JTable
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object cellValue = model.getValueAt(i, j);
                        table.addCell(new PdfPCell(new Phrase(cellValue != null ? cellValue.toString() : "")));
                    }
                }

                // Tambahkan tabel ke dokumen
                document.add(table);
                document.close();

                JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke " + filePath);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal mengekspor data: " + e.getMessage());
            }
        }
    }

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose(); // Menutup jendela saat ini
        MainView.closeInstance(); // Tutup instance yang ada
        MainView.getInstance().setVisible(true); // Buka MainView jika belum terbuka
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStok = new javax.swing.JTable();
        btnKeluar = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        btnEksportPdf = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Stok Barang", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 1, 24))); // NOI18N

        tblStok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Nama Barang", "Kategori", "Jumlah", "Harga", "Tanggal", "Keterangan"
            }
        ));
        jScrollPane1.setViewportView(tblStok);

        btnKeluar.setText("Keluar");

        btnSearch.setText("Cari");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jButton1.setText("Eksport Data TXT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnImport.setText("Import Data TXT");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        btnEksportPdf.setText("Eksport Data PDF");
        btnEksportPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEksportPdfActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnImport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEksportPdf)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(26, 26, 26)
                        .addComponent(btnKeluar)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnKeluar)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(jButton1)
                    .addComponent(btnImport))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEksportPdf)
                .addContainerGap(63, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String keyword = txtSearch.getText(); // Ambil input dari txtSearch

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan kata kunci untuk pencarian!");
        } else {
            // Panggil method pencarian
            ArrayList<String[]> results = BarangMasukController.searchStok(keyword);

            // Tampilkan hasil di tabel
            DefaultTableModel model = (DefaultTableModel) tblStok.getModel(); // Sesuaikan nama tabel Anda
            model.setRowCount(0); // Bersihkan tabel sebelum menampilkan hasil baru

            for (String[] row : results) {
                model.addRow(row); // Tambahkan baris hasil pencarian
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan.");
            }
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        exportToTXT();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        importFromTXT();
    }//GEN-LAST:event_btnImportActionPerformed

    private void btnEksportPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEksportPdfActionPerformed
        exportToPDF();
    }//GEN-LAST:event_btnEksportPdfActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(StokView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StokView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StokView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StokView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StokView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEksportPdf;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblStok;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
