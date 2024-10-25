package com.example.kelompok3_sakuku.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kelompok3_sakuku.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Mendapatkan referensi ke elemen UI
        val akunSaya = view.findViewById<TextView>(R.id.akun_saya)
        val profileImage = view.findViewById<ImageView>(R.id.profile_image)
        val tvName = view.findViewById<TextView>(R.id.tvName) // Tambahkan referensi untuk TextView nama

        // Memuat nama pengguna dari Firestore
        loadUserName(tvName)

        // Fungsi klik untuk Akun Saya
        akunSaya.setOnClickListener {
            val intent = Intent(activity, AkunActivity::class.java)
            startActivity(intent)
        }

        // Fungsi klik untuk gambar profil (contoh)
        profileImage.setOnClickListener {
            Toast.makeText(context, "Klik gambar profil", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun loadUserName(tvName: TextView) {
        userId?.let { uid ->
            db.collection("Users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "Nama tidak tersedia"
                        tvName.text = name // Menampilkan nama di TextView
                    } else {
                        tvName.text = "Profil tidak ditemukan"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Gagal memuat nama pengguna", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            tvName.text = "User ID tidak ditemukan"
        }
    }
}
