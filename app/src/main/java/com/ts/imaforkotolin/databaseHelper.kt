package com.ts.imaforkotolin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // データベース情報
        private const val DATABASE_NAME = "inventory.db"
        private const val DATABASE_VERSION = 3

        // テーブル名
        const val TABLE_NAME = "Inventory"

        // カラム名
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_IS_CHECKED = "isChecked"
        const val COLUMN_CREATED_TIME = "createdTime"
        const val COLUMN_COMMENT = "comment"
        const val COLUMN_IMAGE_STRING = "imageString"
        const val COLUMN_IS_DELETED = "isDeleted"

        // `isChecked` の値を定数化
        const val IS_CHECKED_TRUE = 1
        const val IS_CHECKED_FALSE = 0
        const val DEFAULT_IS_CHECKED = IS_CHECKED_FALSE

        // `isDeleted` のデフォルト値
        const val DEFAULT_IS_DELETED = 0

        // `isChecked` の変換メソッド
        fun intToBoolean(value: Int): Boolean = value != IS_CHECKED_FALSE
        fun booleanToInt(value: Boolean): Int = if (value) IS_CHECKED_TRUE else IS_CHECKED_FALSE
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
        CREATE TABLE IF NOT EXISTS $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_QUANTITY INTEGER NOT NULL,
            $COLUMN_COMMENT TEXT,
            $COLUMN_IS_CHECKED INTEGER NOT NULL DEFAULT $DEFAULT_IS_CHECKED,
            $COLUMN_CREATED_TIME TEXT NOT NULL,
            $COLUMN_IMAGE_STRING TEXT,
            $COLUMN_IS_DELETED INTEGER NOT NULL DEFAULT $DEFAULT_IS_DELETED
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
                put(COLUMN_NAME, name)
                put(COLUMN_QUANTITY, quantity)
                put(COLUMN_IS_CHECKED, DEFAULT_IS_CHECKED)
                put(COLUMN_CREATED_TIME, System.currentTimeMillis().toString())
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
            if (db.isOpen) db.close()
        }
    }

    fun getAllItems(): List<Item> {
        val itemList = mutableListOf<Item>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
            val isCheckedInt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CHECKED))

            val isChecked = intToBoolean(isCheckedInt)
            println("🔍 getAllItems() 取得: id=$id, name=$name, quantity=$quantity, isChecked=$isChecked")

            itemList.add(Item(id, name, quantity, isChecked))
        }
        cursor.close()
        return itemList
    }

    fun updateIsChecked(id: Int, isChecked: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_CHECKED, booleanToInt(isChecked))
        }

        val rowsAffected = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))

        if (rowsAffected > 0) {
            println("✅ updateIsChecked 成功: id=$id, isChecked=$isChecked")
        } else {
            println("⚠️ updateIsChecked で変更なし: id=$id, isChecked=$isChecked")
        }
        db.close()
    }

    fun updateQuantity(name: String, newQuantity: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_QUANTITY, newQuantity)
        }
        db.update(TABLE_NAME, values, "$COLUMN_NAME = ?", arrayOf(name))
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
