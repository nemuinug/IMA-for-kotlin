package com.ts.imaforkotolin

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SubActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private var itemId: Int = -1  // 🔹 `itemId` をクラス変数として保持

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        databaseHelper = DatabaseHelper(this)
        val itemIdText = findViewById<TextView>(R.id.itemIdDetail)
        val titleText = findViewById<TextView>(R.id.itemTitleDetail)
        val quantityText = findViewById<TextView>(R.id.itemQuantityDetail)
        val createdTimeText = findViewById<TextView>(R.id.itemCreatedTime)
        val commentInput = findViewById<EditText>(R.id.itemCommentDetail)
        val itemImage = findViewById<ImageView>(R.id.itemImageDetail)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val backLabel = findViewById<TextView>(R.id.backLabel)

        // インテントからアイテムIDを取得
        itemId = intent.getIntExtra("ITEM_ID", -1)

        // 🔹 itemId == -1 の場合はエラーを防ぐために終了
        if (itemId == -1) {
            Toast.makeText(this, "アイテム情報が見つかりません", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val item = databaseHelper.getItemById(itemId)

        // 🔹 アイテムが `null` の場合は終了
        if (item == null) {
            Toast.makeText(this, "アイテムが見つかりません", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // アイテムの情報を UI に表示
        itemIdText.text = "ID: ${item.id}"
        titleText.text = item.name
        quantityText.text = "数量: ${item.quantity}"
        createdTimeText.text = "作成時間: ${item.createdTime}"
        commentInput.setText(item.comment ?: "コメントなし")

        // 画像をセット
        val imageBitmap = item.image?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        if (imageBitmap != null) {
            itemImage.setImageBitmap(imageBitmap)
        } else {
            itemImage.setImageResource(R.drawable.ic_default_image)  // 🔹 デフォルト画像を設定
        }

        // 保存ボタンの処理
        saveButton.setOnClickListener {
            val newComment = commentInput.text.toString()

            // 🔹 itemId == -1 なら更新しない
            if (itemId == -1) {
                Toast.makeText(this, "無効なアイテムのため更新できません", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            databaseHelper.updateComment(itemId, newComment)
            Toast.makeText(this, "コメントを保存しました", Toast.LENGTH_SHORT).show()
        }

        // 戻るボタン
        backLabel.setOnClickListener {
            finish()
        }
    }
}
