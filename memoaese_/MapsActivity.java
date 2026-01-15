package com.example.memoaese_; // Ganti dengan package name Anda

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Komponen UI
    private LinearLayout permissionContainer;
    private View mapsContainer;
    private Button buttonIzinkanLokasi;
    private Button buttonAmbilLokasi;

    // Komponen Google Maps & Lokasi
    private GoogleMap gMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationClient;

    // Launcher untuk meminta izin lokasi
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Izin diberikan, tampilkan peta dan muat lokasi
                    showMapUI();
                    // getMapAsync perlu dipanggil di sini jika belum pernah dipanggil sebelumnya
                    if (gMap == null) {
                        mapFragment.getMapAsync(this);
                    } else {
                        // Jika peta sudah siap, langsung dapatkan lokasi
                        getCurrentLocation();
                    }
                } else {
                    // Izin ditolak, beri pesan yang jelas
                    Toast.makeText(this, "Izin lokasi ditolak. Fitur peta tidak dapat digunakan.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Inisialisasi komponen UI
        permissionContainer = findViewById(R.id.permission_container);
        mapsContainer = findViewById(R.id.maps_container);
        buttonIzinkanLokasi = findViewById(R.id.button_izinkan_lokasi);
        buttonAmbilLokasi = findViewById(R.id.button_ambil_lokasi);

        // Inisialisasi komponen lokasi
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inisialisasi MapFragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps_container);
        // PENAMBAHAN: Pengaman jika fragment tidak ditemukan
        if (mapFragment == null) {
            Toast.makeText(this, "Error: Map Fragment tidak ditemukan!", Toast.LENGTH_LONG).show();
            finish(); // Tutup activity jika komponen utama error
            return;
        }

        // Set listener untuk tombol
        buttonIzinkanLokasi.setOnClickListener(v -> requestLocationPermission());
        buttonAmbilLokasi.setOnClickListener(v -> ambilLokasiDanBukaMain());

        // Cek izin saat activity dibuat
        checkLocationPermission();
    }

    /**
     * Memeriksa apakah izin lokasi sudah diberikan.
     * Menentukan UI mana yang harus ditampilkan: layar izin atau layar peta.
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Izin sudah ada, langsung tampilkan UI peta dan muat peta
            showMapUI();
            mapFragment.getMapAsync(this);
        } else {
            // Izin belum ada, tampilkan UI permintaan izin
            showPermissionUI();
        }
    }

    /**
     * Memulai proses permintaan izin lokasi kepada pengguna.
     */
    private void requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void showPermissionUI() {
        permissionContainer.setVisibility(View.VISIBLE);
        mapsContainer.setVisibility(View.GONE);
        buttonAmbilLokasi.setVisibility(View.GONE);
    }

    private void showMapUI() {
        permissionContainer.setVisibility(View.GONE);
        mapsContainer.setVisibility(View.VISIBLE);
        buttonAmbilLokasi.setVisibility(View.VISIBLE);
    }

    /**
     * Callback yang dipanggil saat Google Map sudah siap digunakan.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        // Opsi dasar untuk peta
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setCompassEnabled(true);

        // Panggil fungsi untuk mendapatkan lokasi pengguna saat ini
        getCurrentLocation();
    }

    /**
     * Mendapatkan lokasi terakhir pengguna dan memindahkan kamera ke sana.
     * Juga mengaktifkan tombol 'My Location'.
     */
    @SuppressLint("MissingPermission") // Anotasi untuk menandakan kita sudah cek izin sebelumnya
    private void getCurrentLocation() {
        // Cek sekali lagi untuk memastikan izin tidak dicabut saat runtime
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // Keluar jika izin tidak ada
        }

        gMap.setMyLocationEnabled(true); // Menampilkan titik biru lokasi pengguna
        gMap.getUiSettings().setMyLocationButtonEnabled(true); // Menampilkan tombol 'My Location'

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Lokasi ditemukan, pindahkan kamera ke sana
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f)); // Zoom level 15
                    } else {
                        // PENAMBAHAN: Pesan jika lokasi null (GPS mungkin mati)
                        Toast.makeText(this, "Gagal mendapatkan lokasi. Pastikan layanan lokasi (GPS) Anda aktif.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * PERBAIKAN UTAMA:
     * Fungsi ini mengambil koordinat dari pusat peta, lalu membuka MainActivity
     * sambil mengirim data lokasi tersebut.
     */
    private void ambilLokasiDanBukaMain() {
        if (gMap == null) {
            Toast.makeText(this, "Peta belum siap, silakan coba lagi.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil posisi tengah dari kamera peta saat ini
        LatLng centerLocation = gMap.getCameraPosition().target;
        double latitude = centerLocation.latitude;
        double longitude = centerLocation.longitude;

        Toast.makeText(this, "Lokasi berhasil dipilih!", Toast.LENGTH_SHORT).show();

        // Buat Intent untuk membuka MainActivity
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);

        // (Opsional) Kirim data lokasi ke MainActivity jika diperlukan
        intent.putExtra("EXTRA_LATITUDE", latitude);
        intent.putExtra("EXTRA_LONGITUDE", longitude);

        // Mulai MainActivity
        startActivity(intent);

        // Tutup MapsActivity agar pengguna tidak bisa kembali ke sini dengan tombol "Back"
        finish();
    }
}
