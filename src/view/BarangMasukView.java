package view;

import controller.BarangMasukController;
import model.BarangMasuk;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BarangMasukView extends javax.swing.JFrame {

    private BarangMasukController controller;
    private DefaultTableModel model;

    public BarangMasukView() {
        initComponents();
        controller = new BarangMasukController();
        model = (DefaultTableModel) tblBarangMasuk.getModel();
        loadData(); // Memuat data saat form dibuka

        // Event listener untuk tabel
        tblBarangMasuk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = tblBarangMasuk.getSelectedRow();
                if (selectedRow != -1) {
                    txtIdMasuk.setText(model.getValueAt(selectedRow, 0).toString()); // Tampilkan ID
                    txtNamaBarang.setText(model.getValueAt(selectedRow, 1).toString());
                    txtJumlah.setText(model.getValueAt(selectedRow, 2).toString());
                    txtHarga.setText(model.getValueAt(selectedRow, 3).toString());
                    try {
                        // Ambil tanggal dari tabel sebagai String
                        String tanggalString = model.getValueAt(selectedRow, 4).toString();

                        // Ubah String menjadi Date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date tanggal = dateFormat.parse(tanggalString);

                        // Set tanggal ke JDateChooser
                        dateChooserMasuk.setDate(tanggal);
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

    }

    private void tambahBarang() {
        String nama = txtNamaBarang.getText();
        int jumlah = Integer.parseInt(txtJumlah.getText());
        double harga = Double.parseDouble(txtHarga.getText());

        // Ambil tanggal dari JDateChooser
        Date selectedDate = dateChooserMasuk.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String tanggal = (selectedDate != null) ? dateFormat.format(selectedDate) : null;

        // Pastikan tanggal tidak null sebelum menyimpan
        if (tanggal != null) {
            BarangMasuk barang = new BarangMasuk(0, nama, jumlah, harga, tanggal);
            controller.tambahBarangMasuk(barang);
            JOptionPane.showMessageDialog(this, "Barang masuk berhasil ditambahkan!");
            clearFields();
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method untuk menghapus barang masuk
    private void hapusBarang() {
        int selectedRow = tblBarangMasuk.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) model.getValueAt(selectedRow, 0);
            controller.hapusBarangMasuk(id);
            JOptionPane.showMessageDialog(this, "Barang masuk berhasil dihapus!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!");
        }
    }

    private void perbaruiBarang() {
        int selectedRow = tblBarangMasuk.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) model.getValueAt(selectedRow, 0);
            String nama = txtNamaBarang.getText();
            int jumlah;
            double harga;

            // Ambil tanggal dari JDateChooser
            Date selectedDate = dateChooserMasuk.getDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String tanggal = (selectedDate != null) ? dateFormat.format(selectedDate) : null;

            // Validasi input jumlah dan harga
            try {
                jumlah = Integer.parseInt(txtJumlah.getText());
                harga = Double.parseDouble(txtHarga.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah dan harga harus berupa angka!");
                return;
            }

            if (tanggal != null) {
                // Perbarui data di database
                controller.updateBarangMasuk(id, nama, jumlah, harga, tanggal);
                JOptionPane.showMessageDialog(this, "Barang masuk berhasil diperbarui!");

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
        model.setRowCount(0); // Membersihkan tabel sebelum memuat data
        List<BarangMasuk> daftarBarang = controller.getDaftarBarangMasuk();
        for (BarangMasuk barang : daftarBarang) {
            model.addRow(new Object[]{
                barang.getId(),
                barang.getNamaBarang(),
                barang.getJumlah(),
                barang.getHarga(),
                barang.getTanggal()
            });
        }
    }

    // Method untuk membersihkan field input
    private void clearFields() {
        txtNamaBarang.setText("");
        txtJumlah.setText("");
        txtHarga.setText("");
        dateChooserMasuk.setDate(null); // Kosongkan JDateChooser
    }

    // Method untuk memformat tanggal
    private String formatTanggal(Date tanggalInput) {
        if (tanggalInput != null) {
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            return outputFormat.format(tanggalInput);
        }
        return null;
    }

    private void tabelBarangMasukMouseClicked(java.awt.event.MouseEvent evt) {
        int selectedRow = tblBarangMasuk.getSelectedRow();

        if (selectedRow != -1) {
            // Ambil data dari tabel dan masukkan ke textfield
            txtNamaBarang.setText(model.getValueAt(selectedRow, 1).toString());
            txtJumlah.setText(model.getValueAt(selectedRow, 2).toString());
            txtHarga.setText(model.getValueAt(selectedRow, 3).toString());

            // Ambil data tanggal dari tabel dan set ke JDateChooser
            String tanggalString = model.getValueAt(selectedRow, 4).toString();
            try {
                Date tanggal = new SimpleDateFormat("yyyy-MM-dd").parse(tanggalString);
                dateChooserMasuk.setDate(tanggal);
            } catch (ParseException e) {
                e.printStackTrace();
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
        lblHarga = new javax.swing.JLabel();
        lblTanggal = new javax.swing.JLabel();
        txtNamaBarang = new javax.swing.JTextField();
        txtJumlah = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnTampilkan = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBarangMasuk = new javax.swing.JTable();
        btnKeluar = new javax.swing.JButton();
        lblNamaBarang1 = new javax.swing.JLabel();
        txtIdMasuk = new javax.swing.JTextField();
        dateChooserMasuk = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Barang Masuk ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 1, 24))); // NOI18N

        lblNamaBarang.setText("Nama Barang:");

        lblJumlah.setText("Jumlah:");

        lblHarga.setText("Harga");

        lblTanggal.setText("Tanggal Masuk:");

        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnUpdate.setText("Perbarui");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnTampilkan.setText("Tampilkan");
        btnTampilkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilkanActionPerformed(evt);
            }
        });

        tblBarangMasuk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Barang", "Nama Barang", "Jumlah", "Harga", "Tanggal Masuk"
            }
        ));
        jScrollPane1.setViewportView(tblBarangMasuk);

        btnKeluar.setText("Keluar");

        lblNamaBarang1.setText("ID Barang");

        txtIdMasuk.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNamaBarang)
                            .addComponent(lblJumlah)
                            .addComponent(lblHarga)
                            .addComponent(lblTanggal)
                            .addComponent(lblNamaBarang1))
                        .addGap(50, 50, 50)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtIdMasuk)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtJumlah, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                                .addComponent(txtNamaBarang, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtHarga)
                                .addComponent(dateChooserMasuk, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(153, 153, 153)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnTambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnTampilkan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btnTambah)
                            .addGap(16, 16, 16))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(txtIdMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lblNamaBarang1)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnHapus)
                        .addGap(16, 16, 16)
                        .addComponent(btnUpdate)
                        .addGap(16, 16, 16)
                        .addComponent(btnTampilkan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnKeluar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblNamaBarang)
                        .addGap(25, 25, 25)
                        .addComponent(lblJumlah)
                        .addGap(25, 25, 25)
                        .addComponent(lblHarga)
                        .addGap(25, 25, 25)
                        .addComponent(lblTanggal)
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(txtNamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(dateChooserMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        tambahBarang();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        hapusBarang();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        perbaruiBarang();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnTampilkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilkanActionPerformed
        loadData();
    }//GEN-LAST:event_btnTampilkanActionPerformed

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
            java.util.logging.Logger.getLogger(BarangMasukView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BarangMasukView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BarangMasukView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BarangMasukView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BarangMasukView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnTampilkan;
    private javax.swing.JButton btnUpdate;
    private com.toedter.calendar.JDateChooser dateChooserMasuk;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHarga;
    private javax.swing.JLabel lblJumlah;
    private javax.swing.JLabel lblNamaBarang;
    private javax.swing.JLabel lblNamaBarang1;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JTable tblBarangMasuk;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtIdMasuk;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtNamaBarang;
    // End of variables declaration//GEN-END:variables
}
