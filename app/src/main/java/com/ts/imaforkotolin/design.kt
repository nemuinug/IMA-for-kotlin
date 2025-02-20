package com.example.app

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// RecyclerView のセットアップを行う関数
fun setupRecyclerView(context: Context, recyclerView: RecyclerView) {
    recyclerView.layoutManager = LinearLayoutManager(context)

    // 初期データ（数量 0 のアイテム）
    val itemList = listOf(
        Item("アイテム1"),
        Item("アイテム2"),
        Item("アイテム3")
    )

    // RecyclerView に Adapter を適用
    recyclerView.adapter = ItemAdapter(itemList)
}
