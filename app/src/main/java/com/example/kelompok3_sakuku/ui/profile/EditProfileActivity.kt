package com.example.kelompok3_sakuku.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kelompok3_sakuku.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var imgProfile: ImageView
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageButton
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var selectedImageUri: Uri? = null // Untuk menyimpan URI gambar yang dipilih

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        etName = findViewById(R.id.etName)
        imgProfile = findViewById(R.id.imgProfile)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)

        // Memuat data profil saat ini
        loadUserProfile()

        // Simpan perubahan profil saat tombol diklik
        btnSave.setOnClickListener {
            updateProfile()
        }

        // Tombol Kembali
        btnBack.setOnClickListener {
            finish()
        }

        // Tombol untuk mengubah foto profil
        imgProfile.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun loadUserProfile() {
        userId?.let { uid ->
            db.collection("Users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        val photoUrl = document.getString("photoUrl")
                        if (name != null) {
                            etName.setText(name)
                        }
                        // Menampilkan gambar profil
                        if (!photoUrl.isNullOrEmpty()) {
                            Glide.with(this).load(photoUrl).into(imgProfile)
                        } else {
                            imgProfile.setImageResource(R.drawable.ic_profile) // Gambar default jika tidak ada foto
                        }
                    } else {
                        showNameInputDialog()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memuat profil", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data // Mendapatkan URI gambar yang dipilih
            imgProfile.setImageURI(selectedImageUri) // Menampilkan gambar yang dipilih di ImageView
        }
    }

    private fun updateProfile() {
        val newName = etName.text.toString()

        if (newName.isEmpty()) {
            Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        userId?.let { uid ->
            val profileUpdates: MutableMap<String, Any> = hashMapOf("name" to newName)

            // Jika gambar baru dipilih, unggah ke Firebase Storage
            if (selectedImageUri != null) {
                uploadImageToStorage(uid)
            } else {
                updateFirestoreProfile(profileUpdates) // Update Firestore jika tidak ada gambar
            }
        }
    }

    private fun uploadImageToStorage(uid: String) {
        val storageRef = storage.reference.child("profile_images/$uid.jpg")
        storageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Menyimpan URL gambar di Firestore setelah berhasil diunggah
                    updateFirestoreProfile(hashMapOf("photoUrl" to uri.toString()))
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateFirestoreProfile(profileUpdates: Map<String, Any>) {
        userId?.let { uid ->
            db.collection("Users").document(uid).update(profileUpdates)
                .addOnSuccessListener {
                    saveHistory("Profil diperbarui")
                    Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showNameInputDialog() {
        val dialog = EditText(this)
        dialog.hint = "Masukkan nama Anda"

        AlertDialog.Builder(this)
            .setTitle("Input Nama")
            .setView(dialog)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = dialog.text.toString()
                if (newName.isNotEmpty()) {
                    saveNewName(newName)
                } else {
                    Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun saveNewName(newName: String) {
        userId?.let { uid ->
            val userData = hashMapOf("name" to newName)
            db.collection("Users").document(uid).set(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Nama berhasil disimpan", Toast.LENGTH_SHORT).show()
                    etName.setText(newName)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menyimpan nama", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveHistory(message: String) {
        val historyData = hashMapOf(
            "message" to message,
            "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )

        userId?.let { uid ->
            db.collection("Users").document(uid)
                .collection("ProfileHistory").add(historyData)
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000 // Kode untuk permintaan pemilihan gambar
    }
}
