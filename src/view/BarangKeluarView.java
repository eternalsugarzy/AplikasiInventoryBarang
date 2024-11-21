package view;

import controller.BarangKeluarController;
import controller.BarangMasukController;
import model.BarangKeluar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import model.BarangMasuk;
import java.util.List;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class BarangKeluarView extends javax.swing.JFrame {

    private BarangKeluarController controller;
    private BarangMasukController barangMasukController;
    private DefaultTableModel model;

    public BarangKeluarView() {
        initComponents();
        // Inisialisasi controller
        controller = new BarangKeluarController();
        barangMasukController = new BarangMasukController(); // Inisialisasi controller untuk BarangMasuk
        model = (DefaultTableModel) tblBarangKeluar.getModel();
        loadNamaBarang(); // Memuat data ke ComboBox
        loadData(); // Memuat data ke tabel

        // Event listener untuk tabel
        tblBarangKeluar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = tblBarangKeluar.getSelectedRow();
                if (selectedRow != -1) {
                    txtIdKeluar.setText(model.getValueAt(selectedRow, 0).toString());
                    cmbNamaBarang.setSelectedItem(model.getValueAt(selectedRow, 1).toString());
                    txtKategoriKeluar.setText(model.getValueAt(selectedRow, 2).toString());
                    txtJumlahKeluar.setText(model.getValueAt(selectedRow, 3).toString());

                    try {
                        String tanggalString = model.getValueAt(selectedRow, 4).toString();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date tanggal = dateFormat.parse(tanggalString);
                        dateChooserKeluar.setDate(tanggal);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });

        // Tambahkan event listener untuk combobox
        cmbNamaBarang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedBarang = (String) cmbNamaBarang.getSelectedItem();

                // Cek apakah barang dipilih
                if (selectedBarang != null && !selectedBarang.isEmpty()) {
                    // Dapatkan data barang berdasarkan nama
                    BarangMasuk barang = barangMasukController.getBarangByNama(selectedBarang);

                    if (barang != null) {
                        // Set field ID
                        txtIdKeluar.setText(String.valueOf(barang.getId()));

                        // Set field Kategori
                        txtKategoriKeluar.setText(barang.getKategori());

                        // Set field Nama Barang
                        txtNamaBarangKeluar.setText(barang.getNamaBarang());

                        // Set Jumlah
                        txtJumlahKeluar.setText(String.valueOf(barang.getJumlah()));

                        // Set Tanggal ke JDateChooser
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date tanggal = dateFormat.parse(barang.getTanggal());
                            dateChooserKeluar.setDate(tanggal);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        ;

    }

    private void tambahBarangKeluar() {
        String nama = (String) cmbNamaBarang.getSelectedItem();
        String kategori = txtKategoriKeluar.getText();
        int jumlah = Integer.parseInt(txtJumlahKeluar.getText());
        Date tanggal = dateChooserKeluar.getDate();
        if (tanggal == null) {
            JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong!");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tanggalFormatted = sdf.format(tanggal);

        BarangKeluar barang = new BarangKeluar(0, nama, kategori, jumlah, tanggalFormatted);
        controller.tambahBarangKeluar(barang);
        JOptionPane.showMessageDialog(this, "Barang keluar berhasil ditambahkan!");
        clearFields();
        loadData();
    }

    // Method untuk menambahkan barang keluar
    private void hapusBarang() {
        int selectedRow = tblBarangKeluar.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) model.getValueAt(selectedRow, 0);
            controller.hapusBarangKeluar(id);
            JOptionPane.showMessageDialog(this, "Barang keluar berhasil dihapus!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!");
        }
    }

    // Method untuk memperbarui barang keluar
    private void perbaruiBarang() {
        int selectedRow = tblBarangKeluar.getSelectedRow();
        if (selectedRow != -1) {
            // Ambil data dari tabel dan textfield
            int id = (int) model.getValueAt(selectedRow, 0);
            String nama = (String) cmbNamaBarang.getSelectedItem();
            String kategori = txtKategoriKeluar.getText();
            int jumlah;

            // Ambil tanggal dari JDateChooser
            Date selectedDate = dateChooserKeluar.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String tanggal = (selectedDate != null) ? dateFormat.format(selectedDate) : null;

            // Validasi input jumlah
            try {
                jumlah = Integer.parseInt(txtJumlahKeluar.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka!");
                return;
            }

            // Validasi jika kategori atau tanggal kosong
            if (kategori.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kategori tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (tanggal != null) {
                // Perbarui data di database
                controller.updateBarangKeluar(id, nama, kategori, jumlah, tanggal);
                JOptionPane.showMessageDialog(this, "Barang keluar berhasil diperbarui!");

                clearFields();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diperbarui!");
        }
    }

    // Method untuk memuat data dari database ke tabel
    private void loadData() {
        model.setRowCount(0);
        List<BarangKeluar> daftarBarangKeluar = controller.getDaftarBarangKeluar();
        for (BarangKeluar barang : daftarBarangKeluar) {
            model.addRow(new Object[]{
                barang.getId(),
                barang.getNamaBarang(),
                barang.getKategori(),
                barang.getJumlah(),
                barang.getTanggal()
            });
        }
    }

    private void loadNamaBarang() {
        List<BarangMasuk> daftarBarang = barangMasukController.getDaftarBarangMasuk();
        cmbNamaBarang.removeAllItems(); // Hapus item sebelumnya
        for (BarangMasuk barang : daftarBarang) {
            cmbNamaBarang.addItem(barang.getNamaBarang());
        }
    }

    // Method untuk membersihkan field input
    private void clearFields() {
        txtIdKeluar.setText("");
        cmbNamaBarang.setSelectedIndex(0);
        txtKategoriKeluar.setText("");
        txtJumlahKeluar.setText("");
        dateChooserKeluar.setDate(null);
    }

    private String formatTanggal(String tanggalInput) {
        try {
            // Memastikan input mengikuti format "dd/MM/yyyy"
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            inputFormat.setLenient(false); // Validasi ketat
            Date date = inputFormat.parse(tanggalInput);

            // Ubah format ke "yyyy-MM-dd" untuk MySQL
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            return outputFormat.format(date);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah! Gunakan format dd/MM/yyyy (contoh: 12/01/2024)");
            return null;
        }
    }

    private void tabelBarangKeluarMouseClicked(java.awt.event.MouseEvent evt) {
        int selectedRow = tblBarangKeluar.getSelectedRow();

        if (selectedRow != -1) {
            // Ambil data dari tabel dan masukkan ke textfield
            txtNamaBarangKeluar.setText(model.getValueAt(selectedRow, 1).toString());
            txtJumlahKeluar.setText(model.getValueAt(selectedRow, 2).toString());

            // Ambil tanggal dari tabel dan set ke JDateChooser
            String tanggalString = model.getValueAt(selectedRow, 3).toString();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date tanggal = dateFormat.parse(tanggalString);
                dateChooserKeluar.setDate(tanggal);
            } catch (ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memuat tanggal dari tabel!");
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
        lblNamaBarang = new javax.swing.JLabel();
        lblJumlah = new javax.swing.JLabel();
        lblTanggal = new javax.swing.JLabel();
        txtNamaBarangKeluar = new javax.swing.JTextField();
        txtJumlahKeluar = new javax.swing.JTextField();
        btnTambahKeluar = new javax.swing.JButton();
        btnHapusKeluar = new javax.swing.JButton();
        btnUpdateKeluar = new javax.swing.JButton();
        btnTampilkanKeluar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBarangKeluar = new javax.swing.JTable();
        btnKeluar = new javax.swing.JButton();
        lblNamaBarang1 = new javax.swing.JLabel();
        txtIdKeluar = new javax.swing.JTextField();
        dateChooserKeluar = new com.toedter.calendar.JDateChooser();
        cmbNamaBarang = new javax.swing.JComboBox<>();
        lblNamaBarang2 = new javax.swing.JLabel();
        txtKategoriKeluar = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Barang Keluar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 1, 24))); // NOI18N

        lblNamaBarang.setText("Nama Barang:");

        lblJumlah.setText("Jumlah:");

        lblTanggal.setText("Tanggal Keluar:");

        txtNamaBarangKeluar.setEditable(false);

        btnTambahKeluar.setText("Tambah");
        btnTambahKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahKeluarActionPerformed(evt);
            }
        });

        btnHapusKeluar.setText("Hapus");
        btnHapusKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusKeluarActionPerformed(evt);
            }
        });

        btnUpdateKeluar.setText("Perbarui");
        btnUpdateKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateKeluarActionPerformed(evt);
            }
        });

        btnTampilkanKeluar.setText("Tampilkan");
        btnTampilkanKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilkanKeluarActionPerformed(evt);
            }
        });

        tblBarangKeluar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Barang", "Nama Barang", "Kategori", "Jumlah", "Tanggal Keluar"
            }
        ));
        jScrollPane1.setViewportView(tblBarangKeluar);

        btnKeluar.setText("Keluar");

        lblNamaBarang1.setText("ID Barang");

        txtIdKeluar.setEditable(false);

        lblNamaBarang2.setText("Kategori:");

        txtKategoriKeluar.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNamaBarang)
                            .addComponent(lblJumlah)
                            .addComponent(lblNamaBarang1)
                            .addComponent(lblTanggal)
                            .addComponent(lblNamaBarang2))
                        .addGap(50, 50, 50)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtKategoriKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                            .addComponent(txtIdKeluar)
                            .addComponent(txtJumlahKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                            .addComponent(txtNamaBarangKeluar)
                            .addComponent(dateChooserKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(cmbNamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnKeluar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnTambahKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnUpdateKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnTampilkanKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnHapusKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(67, 67, 67))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnTambahKeluar)
                        .addGap(16, 16, 16)
                        .addComponent(btnHapusKeluar)
                        .addGap(16, 16, 16)
                        .addComponent(btnUpdateKeluar)
                        .addGap(16, 16, 16)
                        .addComponent(btnTampilkanKeluar)
                        .addGap(18, 18, 18)
                        .addComponent(btnKeluar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIdKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNamaBarang1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtNamaBarangKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbNamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblNamaBarang))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtKategoriKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNamaBarang2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtJumlahKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblJumlah))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTanggal)
                            .addComponent(dateChooserKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
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

    private void btnTambahKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahKeluarActionPerformed
        tambahBarangKeluar();
    }//GEN-LAST:event_btnTambahKeluarActionPerformed

    private void btnHapusKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusKeluarActionPerformed
        hapusBarang();
    }//GEN-LAST:event_btnHapusKeluarActionPerformed

    private void btnUpdateKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateKeluarActionPerformed
        perbaruiBarang();
    }//GEN-LAST:event_btnUpdateKeluarActionPerformed

    private void btnTampilkanKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilkanKeluarActionPerformed
        loadData();
    }//GEN-LAST:event_btnTampilkanKeluarActionPerformed

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
            java.util.logging.Logger.getLogger(BarangKeluarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BarangKeluarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BarangKeluarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BarangKeluarView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BarangKeluarView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapusKeluar;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnTambahKeluar;
    private javax.swing.JButton btnTampilkanKeluar;
    private javax.swing.JButton btnUpdateKeluar;
    private javax.swing.JComboBox<String> cmbNamaBarang;
    private com.toedter.calendar.JDateChooser dateChooserKeluar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblJumlah;
    private javax.swing.JLabel lblNamaBarang;
    private javax.swing.JLabel lblNamaBarang1;
    private javax.swing.JLabel lblNamaBarang2;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JTable tblBarangKeluar;
    private javax.swing.JTextField txtIdKeluar;
    private javax.swing.JTextField txtJumlahKeluar;
    private javax.swing.JTextField txtKategoriKeluar;
    private javax.swing.JTextField txtNamaBarangKeluar;
    // End of variables declaration//GEN-END:variables
}
