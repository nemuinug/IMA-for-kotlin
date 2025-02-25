package com.ts.imaforkotolin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log  // 追加
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var totalQuantityLabel: TextView
    private lateinit var clearButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🔹 UI コンポーネントの取得
        recyclerView = findViewById(R.id.listid)
        totalQuantityLabel = findViewById(R.id.totalQuantityLabel)
        clearButton = findViewById(R.id.clearButton)

        // 🔹 データベースヘルパーを初期化
        databaseHelper = DatabaseHelper(this)

        // 🔹 RecyclerView のセットアップ
        recyclerView.layoutManager = LinearLayoutManager(this)
        val itemList = databaseHelper.getAllItems().toMutableList()
        adapter = ItemAdapter(itemList)
        recyclerView.adapter = adapter

        // 🔹 クリアボタンの処理
        clearButton.setOnClickListener {
            databaseHelper.resetDatabase(this)
            Toast.makeText(this, "リストをクリアしました", Toast.LENGTH_SHORT).show()
            refreshRecyclerView()
        }

        // 🔹 初期データの読み込み
        refreshRecyclerView()
    }

    private fun refreshRecyclerView() {
        val updatedList = databaseHelper.getAllItems().toMutableList()

        if (::adapter.isInitialized) {
            adapter.updateItems(updatedList)
        } else {
            adapter = ItemAdapter(updatedList)
            recyclerView.adapter = adapter
        }

        // 🔹 合計数を計算して表示
        val totalQuantity = updatedList.sumOf { it.quantity }
        totalQuantityLabel.text = "合計: $totalQuantity"
    }
}
