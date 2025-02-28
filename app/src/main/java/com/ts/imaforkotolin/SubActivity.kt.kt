package com.ts.imaforkotolin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        val titleText = findViewById<TextView>(R.id.itemTitleDetail)
        val quantityText = findViewById<TextView>(R.id.itemQuantityDetail)
        val backButton = findViewById<Button>(R.id.backButton)

        // 受け取ったデータを表示
        val title = intent.getStringExtra("ITEM_TITLE")
        val quantity = intent.getIntExtra("ITEM_QUANTITY", 0)

        titleText.text = title
        quantityText.text = "数量: $quantity"

        backButton.setOnClickListener {
            finish()
        }
    }
}
