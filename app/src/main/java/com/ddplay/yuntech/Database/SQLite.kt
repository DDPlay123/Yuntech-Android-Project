package com.ddplay.yuntech.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLite(context: Context,
             name: String = database,
             factory: SQLiteDatabase.CursorFactory ?= null,
             version: Int = v
) : SQLiteOpenHelper(context, name, factory, version){
    companion object{
        private const val database = "myDatabase"
        private const val TableName = "history"
        private const val v = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TableName(" +
                "_id integer PRIMARY KEY AUTOINCREMENT, " +
                "Variety text, " +
                "Category_img text, " +
                "Time text," +
                "Lat text," +
                "Lng text)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS history")
        onCreate(db)
    }
}