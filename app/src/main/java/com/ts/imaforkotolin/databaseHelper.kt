package com.ts.imaforkotolin

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val databasePath: String = "${context.applicationInfo.dataDir}/databases/$DATABASE_NAME"
    companion object {
        private const val DATABASE_NAME = "inventory.db"
        private const val DATABASE_VERSION = 1

        // テーブル作成SQL
        private const val CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS Inventory (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                comment TEXT,
                isChecked INTEGER NOT NULL,
                createdTime TEXT NOT NULL,
                imageString TEXT,
                isDeleted INTEGER NOT NULL DEFAULT 0
            );
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(CREATE_TABLE_SQL)
            println(" テーブル作成成功")
        } catch (e: Exception) {
            println("❌ テーブル作成失敗: ${e.localizedMessage}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Inventory")
        onCreate(db)
    }

    /**
     * データベースを開くメソッド
     */
    fun openDatabase(): SQLiteDatabase {
        val db = writableDatabase
        println(" データベースを開きました: $databasePath") // ターミナルに表示
        return db
    }

    /**
     * データベースのパスを取得するメソッド
     */
    fun getDatabasePath(): String {
        return databasePath
    }
}
