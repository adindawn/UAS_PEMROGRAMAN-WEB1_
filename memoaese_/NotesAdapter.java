package com.example.memoaese_; // Pastikan nama paket Anda benar

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adapter adalah jembatan antara data (List<Note>) dan tampilan item di RecyclerView.
 * Tugasnya adalah membuat, mengisi, dan mengelola View untuk setiap item dalam daftar.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    // Daftar data yang akan ditampilkan oleh adapter.
    private final List<Note> noteList;

    // Listener untuk menangani klik, akan diimplementasikan oleh Activity.
    private final OnItemClickListener clickListener;

    /**
     * Interface publik yang harus diimplementasikan oleh Activity atau Fragment yang menggunakan adapter ini.
     * Ini adalah cara terbaik untuk mengirim event klik dari adapter kembali ke Activity.
     */
    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    /**
     * Konstruktor utama untuk NotesAdapter.
     * @param noteList Daftar catatan yang akan ditampilkan.
     * @param clickListener Implementasi dari OnItemClickListener (biasanya 'this' dari Activity).
     */
    public NotesAdapter(List<Note> noteList, OnItemClickListener clickListener) {
        this.noteList = noteList;
        this.clickListener = clickListener;
    }

    /**
     * Metode ini dipanggil oleh RecyclerView saat perlu membuat ViewHolder baru.
     * Ini hanya terjadi beberapa kali di awal, bukan setiap kali item muncul di layar.
     * @param parent ViewGroup tempat item baru akan ditambahkan (dalam hal ini, RecyclerView itu sendiri).
     * @param viewType Tipe view (berguna jika ada beberapa jenis layout item).
     * @return Sebuah instance NoteViewHolder yang baru.
     */
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 'Inflate' (membuat) tampilan dari file XML (note_item_layout.xml) menjadi objek View.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item_layout, parent, false);
        return new NoteViewHolder(view);
    }

    /**
     * Metode ini dipanggil oleh RecyclerView untuk menampilkan data pada posisi tertentu.
     * Ini dipanggil setiap kali item di-scroll ke layar.
     * @param holder ViewHolder yang akan diperbarui untuk mewakili item pada posisi yang diberikan.
     * @param position Posisi item dalam set data.
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        // Ambil objek catatan pada posisi saat ini.
        Note currentNote = noteList.get(position);

        // Panggil metode 'bind' di ViewHolder untuk mengatur data dan listener.
        // Ini membuat kode di sini tetap bersih.
        holder.bind(currentNote);
    }

    /**
     * Mengembalikan jumlah total item dalam daftar data.
     * RecyclerView menggunakan ini untuk mengetahui berapa banyak item yang perlu ditampilkan.
     * @return Jumlah catatan dalam noteList.
     */
    @Override
    public int getItemCount() {
        // Pengaman untuk mencegah NullPointerException jika list belum diinisialisasi.
        return noteList != null ? noteList.size() : 0;
    }

    /**
     * Metode publik untuk memperbarui daftar data di adapter dari luar.
     * Sangat penting saat data berubah (misalnya setelah menambah, mengedit, atau menghapus catatan).
     * @param newNotes Daftar catatan yang baru.
     */
    public void updateData(List<Note> newNotes) {
        noteList.clear(); // Hapus data lama
        noteList.addAll(newNotes); // Tambahkan semua data baru
        notifyDataSetChanged(); // Beri tahu RecyclerView bahwa seluruh data telah berubah dan perlu digambar ulang.
    }

    /**
     * ViewHolder mendeskripsikan tampilan item dan metadata tentang tempatnya di dalam RecyclerView.
     * Ini menampung referensi ke View (seperti TextView) untuk menghindari panggilan `findViewById` yang berulang.
     */
    class NoteViewHolder extends RecyclerView.ViewHolder {
        // Deklarasikan komponen UI yang ada di dalam note_item_layout.xml.
        TextView titleTextView;
        TextView contentPreviewTextView;

        /**
         * Konstruktor untuk ViewHolder.
         * @param itemView Tampilan root dari satu item (dalam kasus ini, CardView dari note_item_layout.xml).
         */
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Hubungkan variabel dengan komponen UI di layout menggunakan findViewById.
            // Ini hanya dilakukan sekali saat ViewHolder dibuat, membuatnya sangat efisien.
            titleTextView = itemView.findViewById(R.id.item_note_title);
            contentPreviewTextView = itemView.findViewById(R.id.item_note_content_preview);
        }

        /**
         * Metode untuk "mengikat" data dari objek Note ke dalam View di dalam ViewHolder ini.
         * @param note Objek catatan yang datanya akan ditampilkan.
         */
        void bind(final Note note) {
            // Set teks pada TextView sesuai dengan data dari objek Note.
            titleTextView.setText(note.getTitle());
            contentPreviewTextView.setText(note.getContent());

            // Set OnClickListener untuk seluruh tampilan item (itemView).
            itemView.setOnClickListener(v -> {
                // Pastikan listener tidak null untuk menghindari NullPointerException.
                if (clickListener != null) {
                    // Panggil metode callback di Activity, kirim objek catatan yang diklik.
                    clickListener.onItemClick(note);
                }
            });
        }
    }
}
