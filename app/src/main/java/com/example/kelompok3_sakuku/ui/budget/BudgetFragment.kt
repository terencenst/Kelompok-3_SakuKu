package com.example.kelompok3_sakuku.ui.budget

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kelompok3_sakuku.R
import com.example.kelompok3_sakuku.SetBudgetActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class BudgetFragment : Fragment() {

    private lateinit var budgetList: LinearLayout
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        val btnSetBudget: Button = view.findViewById(R.id.btnSetBudget)
        budgetList = view.findViewById(R.id.budgetList)
        firestore = FirebaseFirestore.getInstance()

        btnSetBudget.setOnClickListener {
            val intent = Intent(requireActivity(), SetBudgetActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SET_BUDGET)
        }

        // Memuat data budget dari Firestore
        loadBudgetData()

        return view
    }

    // Fungsi untuk memuat data dari Firestore dan menampilkan jika ada
    private fun loadBudgetData() {
        firestore.collection("Budgets").get()
            .addOnSuccessListener { result ->
                budgetList.removeAllViews() // Hapus tampilan sebelumnya
                for (document in result) {
                    val name = document.getString("name") ?: ""
                    val amount = document.getLong("amount") ?: 0
                    addBudgetItem(name, amount)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("BudgetFragment", "Gagal memuat data: ${exception.message}")
            }
    }

    // Fungsi untuk menambahkan item budget ke dalam layout
    private fun addBudgetItem(name: String, amount: Long) {
        val budgetItem = LayoutInflater.from(requireContext()).inflate(
            R.layout.item_budget, budgetList, false
        )

        val tvBudgetName: TextView = budgetItem.findViewById(R.id.tvBudgetName)
        val tvBudgetPercentage: TextView = budgetItem.findViewById(R.id.tvBudgetPercentage)

        tvBudgetName.text = name
        tvBudgetPercentage.text = "Rp $amount"

        budgetList.addView(budgetItem)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SET_BUDGET && resultCode == Activity.RESULT_OK) {
            // Reload data setelah budget disimpan
            loadBudgetData()
        }
    }

    companion object {
        private const val REQUEST_CODE_SET_BUDGET = 1
    }
}
