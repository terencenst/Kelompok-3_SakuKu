package com.example.kelompok3_sakuku.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kelompok3_sakuku.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Menambahkan fungsi klik untuk masing-masing tombol
        val riwayat = view.findViewById<TextView>(R.id.riwayat)
        val akunSaya = view.findViewById<TextView>(R.id.akun_saya)
        val aturPin = view.findViewById<TextView>(R.id.atur_pin)
        val pengaturanNotifikasi = view.findViewById<TextView>(R.id.pengaturan_notifikasi)
        val bahasa = view.findViewById<TextView>(R.id.bahasa)
        val bantuanLaporan = view.findViewById<TextView>(R.id.bantuan_laporan)
        val pengaturanAkun = view.findViewById<TextView>(R.id.pengaturan_akun)

        riwayat.setOnClickListener {
            // Aksi ketika Riwayat di klik
            Toast.makeText(context, "Riwayat di klik", Toast.LENGTH_SHORT).show()
        }

        akunSaya.setOnClickListener {
            // Aksi ketika Akun Saya di klik
            Toast.makeText(context, "Akun Saya di klik", Toast.LENGTH_SHORT).show()
        }

        aturPin.setOnClickListener {
            // Aksi ketika Atur PIN di klik
            Toast.makeText(context, "Atur PIN di klik", Toast.LENGTH_SHORT).show()
        }

        pengaturanNotifikasi.setOnClickListener {
            // Aksi ketika Pengaturan Notifikasi di klik
            Toast.makeText(context, "Pengaturan Notifikasi di klik", Toast.LENGTH_SHORT).show()
        }

        bahasa.setOnClickListener {
            // Aksi ketika Bahasa di klik
            Toast.makeText(context, "Bahasa di klik", Toast.LENGTH_SHORT).show()
        }

        bantuanLaporan.setOnClickListener {
            // Aksi ketika Bantuan & Laporan di klik
            Toast.makeText(context, "Bantuan & Laporan di klik", Toast.LENGTH_SHORT).show()
        }

        pengaturanAkun.setOnClickListener {
            // Aksi ketika Pengaturan Akun di klik
            Toast.makeText(context, "Pengaturan Akun di klik", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
