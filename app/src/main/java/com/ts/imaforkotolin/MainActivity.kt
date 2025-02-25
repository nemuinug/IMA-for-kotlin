package com.ts.imaforkotolin

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // データベースを開く
        databaseHelper = DatabaseHelper(this)
        val db = databaseHelper.openDatabase()
        Log.d("DEBUG", "データベースを開きました: ${databaseHelper.getDatabasePath()}")  // ログ出力

        val recyclerView: RecyclerView = findViewById(R.id.listid)

        // `LinearLayoutManager` を設定
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 初期データを追加
        val itemList = listOf(
            Item("アイテム1", 2, false),
            Item("アイテム2", 5, true),
            Item("アイテム3", 1, false)
        )

        Log.d("DEBUG", "RecyclerView に ${itemList.size} 個のアイテムをセット")  // ログ追加

        val adapter = ItemAdapter(itemList)
        recyclerView.adapter = adapter
    }
}
