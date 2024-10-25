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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var balanceListener: ListenerRegistration
    private lateinit var transactionListener: ListenerRegistration
    private lateinit var userNameListener: ListenerRegistration // Tambahkan ini

    private lateinit var txtSaldo: TextView
    private lateinit var txtUserName: TextView // Tambahkan ini untuk menampilkan nama pengguna
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
        txtUserName = view.findViewById(R.id.txtUserName) // Inisialisasi untuk nama pengguna
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
        loadUserName() // Memuat nama pengguna

        return view
    }

    private fun loadUserName() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        userNameListener = db.collection("Users").document(userId)
            .addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    Log.w("HomeFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("name") ?: "Nama tidak tersedia"
                    txtUserName.text = name // Memperbarui TextView dengan nama pengguna
                } else {
                    txtUserName.text = "Nama tidak tersedia"
                }
            }
    }

    private fun loadBalanceAndTransactions() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        balanceListener = db.collection("Balance").document(userId)
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

        transactionListener = db.collection("TransactionHistory").document(userId).collection("transactions")
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w("HomeFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val transactions = mutableListOf<String>()
                querySnapshot?.documents?.forEach { document ->
                    val description = document.getString("description") ?: "Deskripsi tidak ada"
                    val amount = document.getLong("amount") ?: 0
                    transactions.add("$description: Rp $amount")
                }

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, transactions)
                lvTransactions.adapter = adapter
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        balanceListener.remove()
        transactionListener.remove()
        userNameListener.remove() // Hentikan pendengar untuk nama pengguna saat fragment dihancurkan
    }
}
