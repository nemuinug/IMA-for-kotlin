package com.ts.imaforkotolin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "inventory.db"
        private const val DATABASE_VERSION = 3
        private const val TABLE_NAME = "Inventory"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
        CREATE TABLE IF NOT EXISTS Inventory (
            id INTEGER PRIMARY KEY,
            name TEXT NOT NULL,
            quantity INTEGER NOT NULL,
            comment TEXT,
            isChecked INTEGER NOT NULL,
            createdTime TEXT NOT NULL,
            imageString TEXT,
            isDeleted INTEGER NOT NULL DEFAULT 0
        );
    """.trimIndent()
        db.execSQL(createTableQuery)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        println("⚠️ データベースをリセットします")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    fun insertItem(name: String, quantity: Int): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", name)
                put("quantity", quantity)
                put("isChecked", 0)
                put("createdTime", System.currentTimeMillis().toString())
            }

            val newRowId = db.insert(TABLE_NAME, null, values)

            if (newRowId == -1L) {
                println("⚠️ データベース INSERT に失敗: name=$name, quantity=$quantity")
            } else {
                println(" データベースに追加成功: ID=$newRowId, name=$name, quantity=$quantity")
            }

            newRowId
        } catch (e: Exception) {
            println("🚨 SQLite エラー: ${e.message}")
            e.printStackTrace()
            -1
        } finally {
            if (db.isOpen) db.close()
        }
    }



    fun getAllItems(): List<Item> {
        val itemList = mutableListOf<Item>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Inventory", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))  //  IDを取得
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
            val isCheckedInt = cursor.getInt(cursor.getColumnIndexOrThrow("isChecked"))
            val isChecked = (isCheckedInt != 0)  // 0ならfalse, 1ならtrueに変換

            println("🔍 getAllItems() 取得: id=$id, name=$name, quantity=$quantity, isChecked=$isChecked")

            //  IDを含めた `Item` オブジェクトを作成
            itemList.add(Item(id, name, quantity, isChecked))
        }
        cursor.close()
        return itemList
    }




    fun updateIsChecked(id: Int, isChecked: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("isChecked", if (isChecked) 1 else 0)
        }

        val rowsAffected = db.update("Inventory", values, "id = ?", arrayOf(id.toString()))

        if (rowsAffected > 0) {
            println(" updateIsChecked 成功: id=$id, isChecked=$isChecked")
        } else {
            println("⚠️ updateIsChecked で変更なし: id=$id, isChecked=$isChecked")
        }

        db.close()
    }





    fun updateQuantity(name: String, newQuantity: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("quantity", newQuantity)
        }
        db.update("Inventory", values, "name = ?", arrayOf(name))
        db.close()
    }






    fun resetDatabase(context: Context) {
        context.deleteDatabase(DATABASE_NAME)
        println("⚠️ データベースを削除しました: $DATABASE_NAME")

        // 🔹 データベースを削除後に新しく作成する
        val db = this.writableDatabase
        onCreate(db)
    }


}
