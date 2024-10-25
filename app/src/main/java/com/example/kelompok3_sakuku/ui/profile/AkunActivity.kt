package com.example.kelompok3_sakuku.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.kelompok3_sakuku.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AkunActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnBack: ImageButton
    private lateinit var toolbar: Toolbar
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val currentUser = FirebaseAuth.getInstance().currentUser // Ambil objek pengguna saat ini

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_akun)

        // Inisialisasi UI
        imgProfile = findViewById(R.id.imgProfile)
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        btnBack = findViewById(R.id.btnBack) // Inisialisasi btnBack
        toolbar = findViewById(R.id.toolbar)

        // Mengatur Toolbar sebagai ActionBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Menonaktifkan judul default ActionBar

        // Tombol Kembali
        btnBack.setOnClickListener {
            finish() // Kembali ke activity sebelumnya
        }

        // Fungsi untuk memuat profil pengguna dari Firestore
        loadUserProfile()

        // Menampilkan email pengguna dari autentikasi
        tvEmail.text = currentUser?.email ?: "Email tidak tersedia" // Menampilkan email jika ada

        // Tombol Edit Profil
        btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserProfile() {
        userId?.let { uid ->
            db.collection("Users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "Nama tidak tersedia"
                        tvName.text = name

                        // Mengambil URL foto profil
                        val photoUrl = document.getString("photoUrl")
                        if (!photoUrl.isNullOrEmpty()) {
                            Glide.with(this).load(photoUrl).into(imgProfile)
                        } else {
                            imgProfile.setImageResource(R.drawable.ic_profile)
                        }
                    } else {
                        Toast.makeText(this, "Profil tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memuat profil", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

}
