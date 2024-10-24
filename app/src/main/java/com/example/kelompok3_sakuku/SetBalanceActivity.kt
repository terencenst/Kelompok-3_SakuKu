package com.example.kelompok3_sakuku

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SetBalanceActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var btnSave: Button
    private val db = FirebaseFirestore.getInstance()
    private var operation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_balance)

        etAmount = findViewById(R.id.etAmount)
        btnSave = findViewById(R.id.btnSave)

        operation = intent.getStringExtra("operation")

        btnSave.setOnClickListener {
            updateBalance()
        }
    }

    private fun updateBalance() {
        val amount = etAmount.text.toString().toLongOrNull()

        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Masukkan jumlah yang valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Mendapatkan saldo saat ini dari Firestore
        db.collection("Balance").document("userBalance")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val currentBalance = document.getLong("amount") ?: 0
                    val newBalance = when (operation) {
                        "add" -> currentBalance + amount
                        "subtract" -> (currentBalance - amount).coerceAtLeast(0)
                        else -> currentBalance
                    }

                    // Memperbarui saldo di Firestore
                    db.collection("Balance").document("userBalance")
                        .update("amount", newBalance)
                        .addOnSuccessListener {
                            // Menyimpan transaksi setelah saldo diperbarui
                            addTransaction(operation, amount)

                            Toast.makeText(this, "Saldo berhasil diperbarui", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish() // Kembali ke WalletFragment
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Gagal memperbarui saldo", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Dokumen saldo tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengambil saldo", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addTransaction(operation: String?, amount: Long) {
        val transactionType = when (operation) {
            "add" -> "saku bertambah" // Ubah ke "saku bertambah"
            "subtract" -> "saku berkurang" // Ubah ke "saku berkurang"
            else -> return // Jika tidak ada operasi yang valid, keluar dari fungsi
        }

        val transaction = hashMapOf(
            "type" to transactionType,
            "amount" to amount,
            "timestamp" to System.currentTimeMillis() // Waktu saat transaksi dilakukan
        )

        // Menyimpan transaksi ke Firestore
        db.collection("Transactions").add(transaction)
            .addOnSuccessListener {
                // Transaksi berhasil ditambahkan
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan transaksi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
