package com.example.memoaese_;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText editTextJudul, editTextKonten;
    private Button buttonHapus;

    private boolean isEditMode = false;
    private long noteId = -1;
    // Di aplikasi nyata, Anda akan punya objek DatabaseHelper di sini
    // private NoteDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        // Aktifkan tombol kembali (panah) di ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inisialisasi komponen UI dan Database Helper
        editTextJudul = findViewById(R.id.edit_text_judul);
        editTextKonten = findViewById(R.id.edit_text_konten);
        buttonHapus = findViewById(R.id.button_hapus);
        // dbHelper = new NoteDatabaseHelper(this);

        // Cek Intent untuk menentukan apakah ini mode Edit atau Buat Baru
        handleIntent();

        buttonHapus.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("NOTE_ID")) {
            // --- MODE EDIT ---
            isEditMode = true;
            noteId = intent.getLongExtra("NOTE_ID", -1);

            // Ganti judul ActionBar
            setTitle("Edit Catatan");
            // Tampilkan tombol hapus
            buttonHapus.setVisibility(View.VISIBLE);

            // PENAMBAHAN: Muat data dari database dan isi ke dalam EditText
            loadNoteData();
        } else {
            // --- MODE BUAT BARU ---
            isEditMode = false;
            setTitle("Buat Catatan Baru");
            // Sembunyikan tombol hapus
            buttonHapus.setVisibility(View.GONE);
        }
    }

    private void loadNoteData() {
        if (noteId == -1) return;
        // Di aplikasi nyata, Anda akan memanggil database di sini
        // Contoh: Note note = dbHelper.getNoteById(noteId);
        // if (note != null) {
        //     editTextJudul.setText(note.getTitle());
        //     editTextKonten.setText(note.getContent());
        // }

        // Untuk sekarang, kita gunakan data dummy untuk simulasi
        editTextJudul.setText("Judul Catatan yang Diedit");
        editTextKonten.setText("Ini adalah konten catatan lama yang dimuat dari 'database' untuk diedit.");
    }

    // --- Logika Menu (untuk Tombol Simpan) ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Logika untuk tombol Simpan di ActionBar
        if (item.getItemId() == R.id.action_save) {
            saveNote();
            return true;
        }
        // Logika untuk tombol Kembali (panah) di ActionBar
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNote() {
        String judul = editTextJudul.getText().toString().trim();
        String konten = editTextKonten.getText().toString().trim();

        if (judul.isEmpty()) {
            Toast.makeText(this, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            // Logika UPDATE ke database
            // dbHelper.updateNote(noteId, judul, konten);
            Toast.makeText(this, "Catatan diperbarui!", Toast.LENGTH_SHORT).show();
        } else {
            // Logika INSERT ke database
            // dbHelper.insertNote(judul, konten);
            Toast.makeText(this, "Catatan berhasil disimpan!", Toast.LENGTH_SHORT).show();
        }

        finish(); // Kembali ke MainActivity
    }

    private void deleteNote() {
        if (noteId == -1) return;
        // Logika DELETE dari database
        // dbHelper.deleteNote(noteId);
        Toast.makeText(this, "Catatan dihapus!", Toast.LENGTH_SHORT).show();
        finish(); // Kembali ke MainActivity
    }

    // --- Dialog Konfirmasi Hapus ---
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Catatan")
                .setMessage("Apakah Anda yakin ingin menghapus catatan ini? Tindakan ini tidak dapat dibatalkan.")
                .setIcon(R.drawable.ic_delete) // Buat ikon 'ic_delete' jika perlu
                .setPositiveButton("Hapus", (dialog, which) -> deleteNote())
                .setNegativeButton("Batal", null)
                .show();
    }
}
