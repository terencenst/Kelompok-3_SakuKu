package com.example.kelompok3_sakuku

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SetBudgetActivity : AppCompatActivity() {

    private lateinit var etBudgetName: EditText
    private lateinit var etBudgetAmount: EditText
    private lateinit var btnSaveBudget: Button
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_budget)

        etBudgetName = findViewById(R.id.etBudgetName)
        etBudgetAmount = findViewById(R.id.etBudgetAmount)
        btnSaveBudget = findViewById(R.id.btnSaveBudget)

        // Inisialisasi Firestore
        firestore = FirebaseFirestore.getInstance()

        btnSaveBudget.setOnClickListener {
            saveBudgetToFirestore()
        }
    }

    // Fungsi untuk menyimpan data budget ke Firestore
    private fun saveBudgetToFirestore() {
        val name = etBudgetName.text.toString().trim()
        val amount = etBudgetAmount.text.toString().toLongOrNull()

        if (name.isEmpty() || amount == null) {
            Toast.makeText(this, "Nama dan jumlah harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val budget = Budget(name, amount)

        // Simpan data ke Firestore
        firestore.collection("Budgets").add(budget)
            .addOnSuccessListener {
                Toast.makeText(this, "Budget berhasil disimpan", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK) // Kirimkan hasil ke fragment
                finish() // Kembali ke fragment setelah menyimpan
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

// Data class untuk Budget
data class Budget(val name: String, val amount: Long)
