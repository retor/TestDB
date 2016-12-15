package ru.barsopen.testdb.data.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteCursorDriver
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQuery
import ru.barsopen.testdb.data.db.providers.ListProvider
import ru.barsopen.testdb.data.db.providers.QueryProvider

/**
 * Created by d.sokolov on 13.12.2016.
 */
class DBHelper(context: Context, name: String?= "ItemsWithQuery", factory: ((SQLiteDatabase, SQLiteCursorDriver, String, SQLiteQuery) -> Cursor)?=null, version: Int=1) :
        SQLiteOpenHelper(context, name, factory, version) {

    constructor(context:Context):this(context,"ItemsWithQuery")

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(ListProvider.DB_CREATE)
        p0?.execSQL(QueryProvider.DB_CREATE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }
}