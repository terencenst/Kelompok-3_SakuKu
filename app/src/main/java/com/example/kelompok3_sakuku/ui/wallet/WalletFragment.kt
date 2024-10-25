package com.example.kelompok3_sakuku.ui.wallet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kelompok3_sakuku.R
import com.example.kelompok3_sakuku.SetBalanceActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WalletFragment : Fragment() {

    private lateinit var txtBalance: TextView
    private lateinit var btnAdd: Button
    private lateinit var btnSubtract: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)

        txtBalance = view.findViewById(R.id.txtBalance)
        btnAdd = view.findViewById(R.id.btnAdd)
        btnSubtract = view.findViewById(R.id.btnSubtract)

        loadBalance()

        btnAdd.setOnClickListener {
            val intent = Intent(requireActivity(), SetBalanceActivity::class.java)
            intent.putExtra("operation", "add")
            startActivityForResult(intent, REQUEST_CODE_SET_BALANCE)
        }

        btnSubtract.setOnClickListener {
            val intent = Intent(requireActivity(), SetBalanceActivity::class.java)
            intent.putExtra("operation", "subtract")
            startActivityForResult(intent, REQUEST_CODE_SET_BALANCE)
        }

        return view
    }

    private fun loadBalance() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return // Ambil UID pengguna saat ini
        db.collection("Balance").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val balance = document.getLong("amount") ?: 0
                    txtBalance.text = "Saldo: Rp $balance"
                } else {
                    val initialBalanceData = hashMapOf("amount" to 0L)
                    db.collection("Balance").document(userId).set(initialBalanceData)
                        .addOnSuccessListener {
                            txtBalance.text = "Saldo: Rp 0"
                        }
                        .addOnFailureListener {
                            txtBalance.text = "Gagal menginisialisasi saldo"
                        }
                }
            }
            .addOnFailureListener {
                txtBalance.text = "Gagal mengambil saldo"
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SET_BALANCE && resultCode == AppCompatActivity.RESULT_OK) {
            loadBalance() // Memuat saldo setelah update
        }
    }

    companion object {
        private const val REQUEST_CODE_SET_BALANCE = 1
    }
}
