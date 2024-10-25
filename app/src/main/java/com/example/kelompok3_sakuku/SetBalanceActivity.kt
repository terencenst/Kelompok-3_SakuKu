package com.example.kelompok3_sakuku

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SetBalanceActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageButton // Tambahkan ini
    private val db = FirebaseFirestore.getInstance()
    private var operation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_balance)

        etAmount = findViewById(R.id.etAmount)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack) // Inisialisasi tombol Back

        operation = intent.getStringExtra("operation")

        btnBack.setOnClickListener {
            finish() // Kembali ke aktivitas sebelumnya
        }

        btnSave.setOnClickListener {
            if (operation == "subtract") {
                checkCurrentBalanceAndUpdate()
            } else {
                updateBalance()
            }
        }
    }

    private fun checkCurrentBalanceAndUpdate() {
        val amount = etAmount.text.toString().toLongOrNull()

        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Jumlah tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val balanceRef = db.collection("Balance").document(userId)

        balanceRef.get().addOnSuccessListener { document ->
            val currentBalance = document.getLong("amount") ?: 0L

            if (currentBalance < amount) {
                Toast.makeText(this, "Saldo tidak cukup", Toast.LENGTH_SHORT).show()
            } else {
                updateBalance()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal memeriksa saldo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBalance() {
        val amount = etAmount.text.toString().toLongOrNull()

        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Jumlah tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val balanceRef = db.collection("Balance").document(userId)

        // Menentukan deskripsi transaksi
        val transactionDescription = if (operation == "add") "Saku bertambah" else "Saku berkurang"

        // Memperbarui saldo
        if (operation == "add") {
            balanceRef.update("amount", FieldValue.increment(amount))
        } else if (operation == "subtract") {
            balanceRef.update("amount", FieldValue.increment(-amount))
        }

        // Menyimpan riwayat transaksi
        val transactionData = hashMapOf(
            "description" to transactionDescription,
            "amount" to amount,
            "timestamp" to FieldValue.serverTimestamp()
        )
        db.collection("TransactionHistory").document(userId).collection("transactions")
            .add(transactionData)
            .addOnSuccessListener {
                Toast.makeText(this, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan transaksi: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        // Mengembalikan hasil ke HomeFragment
        setResult(Activity.RESULT_OK)
        finish()
    }
}
