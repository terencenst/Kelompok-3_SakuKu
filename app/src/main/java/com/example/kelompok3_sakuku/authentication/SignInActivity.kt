package com.example.kelompok3_sakuku.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kelompok3_sakuku.MainActivity
import com.example.kelompok3_sakuku.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi FirebaseAuth dan SharedPreferences
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        binding.textView.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passET.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Mendapatkan UID pengguna dan menyimpannya ke SharedPreferences
                        val userId = firebaseAuth.currentUser?.uid
                        userId?.let {
                            val editor = sharedPreferences.edit()
                            editor.putString("userId", it) // Menyimpan uid
                            editor.apply()
                        }

                        // Mengarahkan pengguna ke MainActivity setelah login berhasil
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
