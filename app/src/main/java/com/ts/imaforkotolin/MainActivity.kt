package com.ts.imaforkotolin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var totalQuantityLabel: TextView
    private lateinit var clearButton: Button
    private lateinit var addButton: Button  // ✅ 追加
    private lateinit var itemTitle: EditText  // ✅ 追加
    private lateinit var itemQuantity: EditText  // ✅ 追加

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🔹 UI コンポーネントの取得
        recyclerView = findViewById(R.id.listid)
        totalQuantityLabel = findViewById(R.id.totalQuantityLabel)
        clearButton = findViewById(R.id.clearButton)
        addButton = findViewById(R.id.addButton)
        itemTitle = findViewById(R.id.itemTitle)
        itemQuantity = findViewById(R.id.itemQuantity)

        // 🔹 データベースヘルパーを初期化
        databaseHelper = DatabaseHelper(this)

        // 🔹 RecyclerView のセットアップ
        recyclerView.layoutManager = LinearLayoutManager(this)
        val itemList = databaseHelper.getAllItems().toMutableList()
        adapter = ItemAdapter(itemList)
        recyclerView.adapter = adapter

        // 🔹 アイテム追加ボタンの設定
        setupAddButton(this, databaseHelper, adapter, itemTitle, itemQuantity, addButton) {
            refreshRecyclerView()
        }

        // 🔹 クリアボタンの処理
        clearButton.setOnClickListener {
            databaseHelper.resetDatabase(this)
            Toast.makeText(this, "リストをクリアしました", Toast.LENGTH_SHORT).show()
            refreshRecyclerView()
        }

        // 🔹 初期データの読み込み
        refreshRecyclerView()
    }

    fun refreshRecyclerView() {
        val updatedList = databaseHelper.getAllItems().toMutableList()
        adapter.updateItems(updatedList)

        val checkedQuantity = updatedList.filter { it.isChecked }.sumOf { it.quantity }

        runOnUiThread {
            totalQuantityLabel.text = "合計: $checkedQuantity"
            adapter.notifyDataSetChanged()  // RecyclerView全体をリフレッシュ
        }
    }

    fun updateTotalQuantity() {
        val updatedList = databaseHelper.getAllItems().toMutableList()

        // ✅ isChecked が true のアイテムのみ合計
        val checkedItems = updatedList.filter { it.isChecked }

        println("🔍 updateTotalQuantity() 実行 - チェックされたアイテム:")
        checkedItems.forEach { item ->
            println("✅ id=${item.id}, name=${item.name}: 数量=${item.quantity}")
        }


        val totalQuantity = checkedItems.sumOf { it.quantity }

        runOnUiThread {
            totalQuantityLabel.text = "合計: $totalQuantity"
        }
    }










}
