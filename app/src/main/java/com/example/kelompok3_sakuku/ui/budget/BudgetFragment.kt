package com.example.kelompok3_sakuku.ui.budget

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kelompok3_sakuku.R
import com.example.kelompok3_sakuku.SetBudgetActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class BudgetFragment : Fragment() {

    private lateinit var txtBalance: TextView // Tambahkan ini untuk saldo
    private lateinit var budgetList: LinearLayout
    private lateinit var firestore: FirebaseFirestore
    private lateinit var balanceListener: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        // Inisialisasi tampilan
        txtBalance = view.findViewById(R.id.tvBalance) // Mengambil TextView untuk saldo
        val btnSetBudget: TextView = view.findViewById(R.id.btnSetBudget) // Ganti menjadi TextView
        budgetList = view.findViewById(R.id.budgetList)
        firestore = FirebaseFirestore.getInstance()

        // Aksi ketika teks "Set Budget" ditekan
        btnSetBudget.setOnClickListener {
            val intent = Intent(requireActivity(), SetBudgetActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SET_BUDGET)
        }

        // Memuat data budget dan saldo dari Firestore
        loadBudgetData()
        loadBalance() // Memanggil fungsi untuk memuat saldo

        return view
    }

    // Fungsi untuk memuat data budget dari Firestore
    private fun loadBudgetData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid // Ambil UID pengguna saat ini
        firestore.collection("Budgets").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { result ->
                budgetList.removeAllViews()
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

    // Fungsi untuk memuat saldo dari Firestore
    private fun loadBalance() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        balanceListener = firestore.collection("Balance").document(userId)
            .addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    Log.w("BudgetFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val balance = documentSnapshot.getLong("amount") ?: 0
                    txtBalance.text = "Rp $balance" // Memperbarui tampilan saldo
                } else {
                    // Jika dokumen tidak ada, buat dokumen dengan saldo awal
                    val initialBalanceData = hashMapOf("amount" to 0L)
                    firestore.collection("Balance").document(userId).set(initialBalanceData)
                        .addOnSuccessListener {
                            txtBalance.text = "Rp 0" // Jika saldo baru diinisialisasi
                        }
                        .addOnFailureListener {
                            txtBalance.text = "Gagal menginisialisasi saldo"
                        }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SET_BUDGET && resultCode == Activity.RESULT_OK) {
            // Reload data setelah budget disimpan
            loadBudgetData()
            // Tidak perlu memanggil loadBalance() lagi karena sudah menggunakan listener
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan pendengar saat fragment dihancurkan
        balanceListener.remove()
    }

    companion object {
        private const val REQUEST_CODE_SET_BUDGET = 1
    }
}
