package com.ts.imaforkotolin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        // UI要素を取得
        val itemImage = findViewById<ImageView>(R.id.itemImage)
                val itemTitle = findViewById<TextView>(R.id.itemTitle)
                val itemQuantity = findViewById<TextView>(R.id.itemQuantity)
                val itemComment = findViewById<TextView>(R.id.itemComment)
                val backButton = findViewById<Button>(R.id.backButton)

                // Intentからデータを取得
                val name = intent.getStringExtra("ITEM_NAME") ?: "不明"
        val quantity = intent.getIntExtra("ITEM_QUANTITY", 0)
        val comment = intent.getStringExtra("ITEM_COMMENT") ?: "コメントなし"
        val imageResId = intent.getIntExtra("ITEM_IMAGE", R.drawable.ic_launcher_foreground)

        // 画面にデータを表示
        itemTitle.text = name
        itemQuantity.text = "数量: $quantity"
        itemComment.text = comment
        itemImage.setImageResource(imageResId)

        // 戻るボタンの動作
        backButton.setOnClickListener {
            finish()
        }
    }
}
