package com.ts.imaforkotolin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "inventory.db"
        private const val DATABASE_VERSION = 5
        const val TABLE_NAME = "Inventory"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_IS_CHECKED = "isChecked"
        const val COLUMN_CREATED_TIME = "createdTime"
        const val COLUMN_COMMENT = "comment"
        const val COLUMN_IMAGE = "image"  // 🔹 BLOB を使う
        const val COLUMN_IS_DELETED = "isDeleted"
        const val IS_CHECKED_TRUE = 1
        const val IS_CHECKED_FALSE = 0
        const val IS_DELETED_FALSE = 0

        fun intToBoolean(value: Int): Boolean = value != IS_CHECKED_FALSE
        fun booleanToInt(value: Boolean): Int = if (value) IS_CHECKED_TRUE else IS_CHECKED_FALSE
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
        CREATE TABLE IF NOT EXISTS $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_QUANTITY INTEGER NOT NULL DEFAULT 1,
            $COLUMN_IS_CHECKED INTEGER NOT NULL DEFAULT $IS_CHECKED_FALSE,
            $COLUMN_CREATED_TIME TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
            $COLUMN_COMMENT TEXT,
            $COLUMN_IMAGE BLOB,  
            $COLUMN_IS_DELETED INTEGER NOT NULL DEFAULT $IS_DELETED_FALSE
        );
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        println("⚠️ データベースをリセットします")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertItem(name: String, quantity: Int, comment: String? = null, image: ByteArray? = null): Long {
        val db = writableDatabase
        return try {
            val defaultImage = image ?: bitmapToByteArray(getDefaultImage())

            val values = ContentValues().apply {
                put(COLUMN_NAME, name)
                put(COLUMN_QUANTITY, quantity)
                put(COLUMN_IS_CHECKED, IS_CHECKED_FALSE)
                put(COLUMN_COMMENT, comment)
                put(COLUMN_IMAGE, defaultImage)
            }

            val newRowId = db.insert(TABLE_NAME, null, values)
            newRowId
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        } finally {
            db.close()
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
            val isChecked = isCheckedInt != 0  // 🔹 Boolean に変換
            val createdTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_TIME))
            val comment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT))
            val imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))

            val image = imageBlob ?: bitmapToByteArray(getDefaultImage())  // 🔹 デフォルト画像

            itemList.add(Item(id, name, quantity, isChecked, createdTime, comment, image))
        }
        cursor.close()
        return itemList
    }


    fun getItemById(id: Int): Item? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE id = ?", arrayOf(id.toString()))

        return if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
            val isCheckedInt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CHECKED))
            val isChecked = isCheckedInt != 0
            val createdTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_TIME)) ?: "N/A"
            val comment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT))
            val imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))

            val image = imageBlob ?: bitmapToByteArray(getDefaultImage())

            cursor.close()
            Item(id, name, quantity, isChecked, createdTime, comment, image)
        } else {
            cursor.close()
            return null  // 🔹 例外を投げず、null を返す
        }
    }

    fun updateComment(id: Int, newComment: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_COMMENT, newComment)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun updateIsChecked(id: Int, isChecked: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_CHECKED, booleanToInt(isChecked))
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
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

    fun updateImageBlob(id: Int, newImage: ByteArray) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IMAGE, newImage)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun resetDatabase(mainActivity: MainActivity) {
        context.deleteDatabase(DATABASE_NAME)
        val db = this.writableDatabase
        onCreate(db)
        println("⚠️ データベースを削除しました: $DATABASE_NAME")
    }

    fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun getDefaultImage(): Bitmap {
        return BitmapFactory.decodeResource(context.resources, R.drawable.ic_default_image)
    }
}
