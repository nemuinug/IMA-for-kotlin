package com.ts.imaforkotolin

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log

fun setupRecyclerView(context: Context, recyclerView: RecyclerView, databaseHelper: DatabaseHelper, onCheckedChange: () -> Unit) {
    recyclerView.layoutManager = LinearLayoutManager(context)

    // 🔹 データベースからアイテムを取得
    val itemList = databaseHelper.getAllItems().toMutableList()
    Log.d("DEBUG", "データベースから ${itemList.size} 個のアイテムを取得")

    recyclerView.adapter = ItemAdapter(itemList, onCheckedChange)
}