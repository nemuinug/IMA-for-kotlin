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
    private lateinit var addButton: Button
    private lateinit var itemTitle: EditText
    private lateinit var itemQuantity: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)

        recyclerView = findViewById(R.id.listid)
        totalQuantityLabel = findViewById(R.id.totalQuantityLabel)
        clearButton = findViewById(R.id.clearButton)
        addButton = findViewById(R.id.addButton)
        itemTitle = findViewById(R.id.itemTitle)
        itemQuantity = findViewById(R.id.itemQuantity)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val itemList = databaseHelper.getAllItems().toMutableList()

        adapter = ItemAdapter(itemList) {
            updateTotalQuantity()
        }

        recyclerView.adapter = adapter

        setupAddButton(this, databaseHelper, adapter, itemTitle, itemQuantity, addButton) {
            refreshRecyclerView()
        }

        clearButton.setOnClickListener {
            databaseHelper.resetDatabase(this)
            Toast.makeText(this, "リストをクリアしました", Toast.LENGTH_SHORT).show()
            refreshRecyclerView()
        }
        refreshRecyclerView()
        updateTotalQuantity()

        setupRecyclerView(this, recyclerView, databaseHelper) {
            updateTotalQuantity()
        }
    }


    fun refreshRecyclerView() {
        val updatedList = databaseHelper.getAllItems().toMutableList()
        adapter.updateItems(updatedList)

        val checkedQuantity = updatedList.filter { it.isChecked }.sumOf { it.quantity }

        runOnUiThread {
            updateTotalQuantity()
            totalQuantityLabel.text = "合計: $checkedQuantity"
        }
    }

    fun updateTotalQuantity() {
        val updatedList = databaseHelper.getAllItems().toMutableList()
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
