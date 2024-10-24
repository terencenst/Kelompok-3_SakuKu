package com.example.kelompok3_sakuku.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kelompok3_sakuku.R
import com.example.kelompok3_sakuku.NotificationsActivity
import com.example.kelompok3_sakuku.DetailActivity

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi view
        val btnNotification = view.findViewById<ImageButton>(R.id.btnNotification)
        val btnDetail = view.findViewById<Button>(R.id.btnDetail)
        val txtSaldo = view.findViewById<TextView>(R.id.txtSaldo)
        val lvTransactions = view.findViewById<ListView>(R.id.lvTransactions)

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

        // Contoh data transaction history
        val transactions = listOf("Belanja: Rp 150.000", "Makan: Rp 50.000", "Tabungan: Rp 500.000")
        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            transactions
        )
        lvTransactions.adapter = adapter

        return view
    }
}
