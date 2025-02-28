package com.ts.imaforkotolin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper

class MainActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var totalQuantityLabel: TextView
    private lateinit var clearButton: Button
    private lateinit var addButton: Button
    private lateinit var itemTitle: EditText
    private lateinit var itemQuantity: EditText
    companion object {
        private const val REQUEST_CODE_SUB_ACTIVITY = 1001  // 適当な整数を指定
    }

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
            databaseHelper.resetDatabase()  // 論理削除

            Toast.makeText(this, "リストをクリアしました", Toast.LENGTH_SHORT).show()

            adapter.updateItems(emptyList())  // リストを空にする
            recyclerView.visibility = View.GONE  // RecyclerView を非表示に
        }


        refreshRecyclerView()
        updateTotalQuantity()

        // setupRecyclerView() の呼び出しを削除し、最初に設定したアダプタを統一
        adapter = ItemAdapter(itemList) {
            updateTotalQuantity()
        }
        recyclerView.adapter = adapter

        // **スワイプ処理を追加**
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val itemToDelete = adapter.getItemAt(position)

                databaseHelper.deleteItemById(itemToDelete.id)  // **DBで論理削除**
                refreshRecyclerView()  // **リストを更新**

                Toast.makeText(applicationContext, "アイテムを削除しました", Toast.LENGTH_SHORT).show()
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SUB_ACTIVITY && resultCode == Activity.RESULT_OK) {
            val updatedItemId = data?.getIntExtra("UPDATED_ITEM_ID", -1)
            if (updatedItemId != null && updatedItemId != -1) {
                refreshRecyclerView()  // コメントや画像を即時更新
            }
        }
    }

    fun refreshRecyclerView() {
        val updatedList = databaseHelper.getAllItems().toMutableList()

        if (updatedList.isEmpty()) {
            recyclerView.visibility = View.GONE  // **リストが空なら非表示**
        } else {
            recyclerView.visibility = View.VISIBLE  // **アイテムがあるなら表示**
        }

        adapter.updateItems(updatedList)  // **リスト更新**
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
