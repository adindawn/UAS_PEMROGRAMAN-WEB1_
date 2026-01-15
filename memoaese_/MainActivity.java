package com.example.memoaese_; // Pastikan nama paket Anda benar

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity adalah layar utama aplikasi yang menampilkan daftar semua catatan pengguna.
 * Pengguna dapat melihat, menambah, atau memilih catatan untuk dilihat detailnya.
 */
public class MainActivity extends AppCompatActivity {

    // Komponen UI
    private RecyclerView notesRecyclerView;
    private FloatingActionButton fabAddNote;

    // Data & Adapter
    private NotesAdapter notesAdapter;
    private List<Note> noteList;
    // Di aplikasi nyata, Anda akan memiliki objek DatabaseHelper atau ViewModel di sini
    // private NoteDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Inisialisasi Komponen ---
        // Menghubungkan variabel dengan komponen di layout XML.
        notesRecyclerView = findViewById(R.id.notes_recycler_view);
        // PASTIKAN ID DI XML adalah 'button_add_note' agar konsisten
        fabAddNote = findViewById(R.id.fab_add_note);

        // --- Menyiapkan Logika Aplikasi ---
        // Menerima data lokasi jika ada yang dikirim dari MapsActivity
        handleIncomingIntent();

        // Mengatur RecyclerView dan mengisinya dengan data awal
        setupRecyclerView();

        // Menangani aksi klik pada tombol tambah (+)
        setupFabClickListener();
    }

    /**
     * Mengambil dan menangani data yang dikirim dari MapsActivity.
     */
    private void handleIncomingIntent() {
        Intent intent = getIntent();
        // Memeriksa apakah Intent ada dan berisi data yang kita harapkan (EXTRA_LATITUDE)
        if (intent != null && intent.hasExtra("EXTRA_LATITUDE")) {
            double latitude = intent.getDoubleExtra("EXTRA_LATITUDE", 0.0);
            double longitude = intent.getDoubleExtra("EXTRA_LONGITUDE", 0.0);

            // Menampilkan pesan singkat (Toast) bahwa lokasi telah diterima.
            String locationInfo = "Lokasi Diterima: Lat: " + String.format("%.4f", latitude) + ", Lng: " + String.format("%.4f", longitude);
            Toast.makeText(this, locationInfo, Toast.LENGTH_LONG).show();

            // Di aplikasi nyata, Anda mungkin ingin menyimpan data lokasi ini
            // ke SharedPreferences atau menggunakannya untuk membuat catatan baru.
        }
    }

    /**
     * Menginisialisasi dan mengatur semua yang berhubungan dengan RecyclerView.
     */
    private void setupRecyclerView() {
        // Inisialisasi daftar kosong.
        noteList = new ArrayList<>();
        // Inisialisasi adapter dengan daftar dan listener untuk menangani klik pada item.
        notesAdapter = new NotesAdapter(noteList, new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                // Saat sebuah item catatan di-klik, buka ViewNoteActivity.
                Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
                // Mengirim ID catatan agar ViewNoteActivity tahu data mana yang harus ditampilkan.
                intent.putExtra("NOTE_ID", note.getId());
                startActivity(intent);
            }
        });

        // Mengatur bagaimana item akan ditampilkan (dalam kasus ini, secara vertikal).
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Menghubungkan adapter dengan RecyclerView.
        notesRecyclerView.setAdapter(notesAdapter);
    }

    /**
     * Mengatur listener untuk Floating Action Button (FAB).
     */
    private void setupFabClickListener() {
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Saat tombol '+' diklik, buka layar untuk membuat catatan baru.
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Mengisi RecyclerView dengan data contoh.
     * Dalam aplikasi nyata, metode ini akan digantikan dengan pemanggilan ke database.
     * `databaseHelper.getAllNotes()`
     */
    private void loadNotesFromSource() {
        // Membersihkan daftar yang ada untuk menghindari duplikasi saat memuat ulang.
        noteList.clear();

        // Menambahkan data contoh. Ganti ini dengan data dari database Anda.
        noteList.add(new Note(1, "NOTE TITLE 1", "Ini adalah isi dari catatan 1."));
        noteList.add(new Note(2, "NOTE TITLE 2", "Ini adalah isi dari catatan 2."));
        noteList.add(new Note(3, "NOTE TITLE 3", "Ini adalah isi dari catatan 3."));
    }

    /**
     * Metode ini dipanggil setiap kali Activity kembali aktif (misalnya, setelah kembali
     * dari CreateNoteActivity). Ini adalah tempat terbaik untuk memuat ulang data.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Memuat atau memuat ulang data dari sumbernya (database atau data dummy).
        loadNotesFromSource();

        // Memberi tahu adapter bahwa data telah berubah agar RecyclerView dapat diperbarui.
        // Pengaman 'if' untuk mencegah NullPointerException jika adapter belum siap.
        if (notesAdapter != null) {
            notesAdapter.notifyDataSetChanged();
        }
    }
}
