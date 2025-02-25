package com.ts.imaforkotolin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "inventory.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "Inventory"
    }

    // ✅ 正しい onCreate (データベースのテーブル作成)
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                quantity INTEGER NOT NULL CHECK(quantity >= 0)
            );
        """.trimIndent()
        db.execSQL(createTableQuery)
        println("✅ データベースを作成しました")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        println("⚠️ データベースをリセットします")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // 🔹 データベースを開くメソッド
    fun openDatabase(): SQLiteDatabase {
        return this.writableDatabase
    }

    fun insertItem(name: String, quantity: Int): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", name)
                put("quantity", quantity)
            }

            val newRowId = db.insert(TABLE_NAME, null, values)

            if (newRowId == -1L) {
                println("⚠️ データベース INSERT に失敗: name=$name, quantity=$quantity")
            } else {
                println("✅ データベースに追加成功: ID=$newRowId, name=$name, quantity=$quantity")
            }

            newRowId
        } catch (e: Exception) {
            println("🚨 SQLite エラー: ${e.message}")
            e.printStackTrace()
            -1
        } finally {
            if (db.isOpen) db.close() // ✅ クローズ前にチェック
        }
    }


    // 🔹 すべてのアイテムを取得
    fun getAllItems(): List<Item> {
        val itemList = mutableListOf<Item>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        println("📌 データベース内のアイテム数: ${cursor.count}")

        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
            println("📌 取得したアイテム: name=$name, quantity=$quantity")
            itemList.add(Item(name, quantity))
        }
        cursor.close()
        return itemList
    }

    fun resetDatabase(context: Context) {
        context.deleteDatabase(DATABASE_NAME)
        println("⚠️ データベースを削除しました: $DATABASE_NAME")

        // 🔹 データベースを削除後に新しく作成する
        val db = this.writableDatabase
        onCreate(db)
    }


}
