package com.example.kelompok3_sakuku.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kelompok3_sakuku.R
import com.example.kelompok3_sakuku.NotificationsActivity
import com.example.kelompok3_sakuku.DetailActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var balanceListener: ListenerRegistration
    private lateinit var transactionListener: ListenerRegistration

    private lateinit var txtSaldo: TextView
    private lateinit var lvTransactions: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance()

        // Inisialisasi view
        val btnNotification = view.findViewById<ImageButton>(R.id.btnNotification)
        val btnDetail = view.findViewById<Button>(R.id.btnDetail)
        txtSaldo = view.findViewById(R.id.txtSaldo)
        lvTransactions = view.findViewById(R.id.lvTransactions)

        // Aksi ketika button notifikasi ditekan
        btnNotification.setOnClickListener {
            val intent = Intent(activity, NotificationsActivity::class.java)
            startActivity(intent)
        }

        // Aksi ketika button detail ditekan
        btnDetail.setOnClickListener {
            val intent = Intent(activity, DetailActivity::class.java)
            startActivity(intent)
        }

        // Memuat saldo dan riwayat transaksi
        loadBalanceAndTransactions()

        return view
    }

    private fun loadBalanceAndTransactions() {
        // Mendengarkan perubahan saldo dari Firestore
        balanceListener = db.collection("Balance").document("userBalance")
            .addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    Log.w("HomeFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val balance = documentSnapshot.getLong("amount") ?: 0
                    txtSaldo.text = "Saldo: Rp $balance"
                } else {
                    txtSaldo.text = "Saldo: Rp 0"
                }
            }

        // Memuat riwayat transaksi
        transactionListener = db.collection("Transactions")
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w("HomeFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                // Menampilkan transaksi di ListView
                val transactions = mutableListOf<String>()
                querySnapshot?.documents?.forEach { document ->
                    val type = document.getString("type")
                    val amount = document.getLong("amount") ?: 0
                    transactions.add("$type: Rp $amount")
                }

                // Perbarui ListView dengan transaksi
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, transactions)
                lvTransactions.adapter = adapter
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan pendengar saat fragment dihancurkan
        balanceListener.remove()
        transactionListener.remove()
    }
}
