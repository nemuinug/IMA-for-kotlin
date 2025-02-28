package com.ts.imaforkotolin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.File

class SubActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private var itemId: Int = -1
    private lateinit var itemImage: ImageView

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        databaseHelper = DatabaseHelper(this)
        val itemIdText = findViewById<TextView>(R.id.itemIdDetail)
        val titleText = findViewById<TextView>(R.id.itemTitleDetail)
        val quantityText = findViewById<TextView>(R.id.itemQuantityDetail)
        val createdTimeText = findViewById<TextView>(R.id.itemCreatedTime)
        val commentInput = findViewById<EditText>(R.id.itemCommentDetail)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val backLabel = findViewById<TextView>(R.id.backLabel)
        itemImage = findViewById(R.id.itemImageDetail)

        // インテントからアイテムIDを取得
        itemId = intent.getIntExtra("ITEM_ID", -1)

        if (itemId == -1) {
            Toast.makeText(this, "アイテム情報が見つかりません", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val item = databaseHelper.getItemById(itemId)

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
        setItemImage(item.image)

        // **画像タップでギャラリーを開く**
        itemImage.setOnClickListener {
            openGallery()
        }

        // 保存ボタンの処理
        saveButton.setOnClickListener {
            val newComment = commentInput.text.toString()
            databaseHelper.updateComment(itemId, newComment)
            Toast.makeText(this, "コメントを保存しました", Toast.LENGTH_SHORT).show()

            // **変更を MainActivity に通知**
            val resultIntent = Intent()
            resultIntent.putExtra("UPDATED_ITEM_ID", itemId)
            setResult(Activity.RESULT_OK, resultIntent)

            finish()  // 画面を閉じる
        }

        backLabel.setOnClickListener {
            finish()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                handleImageSelection(imageUri)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun handleImageSelection(imageUri: Uri) {
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            }

            // 画像をデータベースに保存
            saveImageToDatabase(bitmap)

            // ImageView に反映
            itemImage.setImageBitmap(bitmap)

            Log.d("DEBUG", "画像を選択しました: $imageUri")

        } catch (e: Exception) {
            Log.e("ERROR", "画像の取得に失敗", e)
        }
    }



    // **ギャラリーから画像を選択後の処理**
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                saveImageToDatabase(bitmap)
                itemImage.setImageBitmap(bitmap) // 選択した画像をUIに反映
            }
        }
    }

    // **画像をデータベースに保存**
    private fun saveImageToDatabase(bitmap: Bitmap) {
        val byteArray = bitmapToByteArray(bitmap)
        databaseHelper.updateImageBlob(itemId, byteArray)
        Toast.makeText(this, "画像を更新しました", Toast.LENGTH_SHORT).show()

        // **更新結果を MainActivity に通知**
        val resultIntent = Intent()
        resultIntent.putExtra("UPDATED_ITEM_ID", itemId)
        setResult(Activity.RESULT_OK, resultIntent)

        finish()  // 画面を閉じる
    }


    // **Bitmap を ByteArray に変換**
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    // **画像データを ImageView にセット**
    private fun setItemImage(imageBlob: ByteArray?) {
        if (imageBlob != null) {
            Log.d("DEBUG", "画像データを取得: ${imageBlob.size} bytes") // 🔍 ログ出力
            val bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.size)
            itemImage.setImageBitmap(bitmap)
        } else {
            Log.d("DEBUG", "画像データなし、デフォルト画像を設定")
            itemImage.setImageResource(R.drawable.ic_default_image) // デフォルト画像
        }
    }
}
