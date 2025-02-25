package com.ts.imaforkotolin

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log

fun setupRecyclerView(context: Context, recyclerView: RecyclerView) {
    recyclerView.layoutManager = LinearLayoutManager(context)

    val itemList = listOf(
        Item("アイテム1"),
        Item("アイテム2"),
        Item("アイテム3")
    )

    Log.d("DEBUG", "RecyclerView に ${itemList.size} 個のアイテムをセット")  //  デバッグログ

    recyclerView.adapter = ItemAdapter(itemList)
}
