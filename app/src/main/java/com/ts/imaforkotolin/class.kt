package com.ts.imaforkotolin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

fun setupAddButton(
    context: Context,
    databaseHelper: DatabaseHelper,
    adapter: RecyclerView.Adapter<*>,
    nameInput: EditText,
    quantityInput: EditText,
    addButton: Button,
    refreshRecyclerView: () -> Unit  // ✅ コールバックでリスト更新
) {
    addButton.setOnClickListener {
        val name = nameInput.text.toString().trim()  // 修正
        val quantity = quantityInput.text.toString().toIntOrNull() ?: 1

        if (name.isNotEmpty()) {
            val newItemId = databaseHelper.insertItem(name, quantity)

            if (newItemId != -1L) {
                nameInput.text.clear()
                quantityInput.text.clear()

                refreshRecyclerView()  // ✅ 最新のデータを取得して表示を更新
                Toast.makeText(context, "アイテムを追加しました", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "アイテム追加に失敗しました", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "タイトルを入力してください", Toast.LENGTH_SHORT).show()
        }
    }
}
