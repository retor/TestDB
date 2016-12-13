package ru.barsopen.testdb.data.db.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import ru.barsopen.testdb.data.db.DBHelper


/**
 * Created by d.sokolov on 13.12.2016.
 */

class ListProvider : ContentProvider() {

    companion object {
        // // Константы для БД
        // БД
        val DB_NAME = "listBD"
        val DB_VERSION = 1

        // Таблица
        val LIST_TABLE = "listTable"

        // Поля
        val ITEM_ID = "_id"
        val ITEM_TITLE = "title"
        val ITEM_DESCRIPTION = "desc"
        val ITEM_DATE = "date"

        // Скрипт создания таблицы
        val DB_CREATE = "create table ${LIST_TABLE}(${ITEM_ID} integer primary key autoincrement, " +
                "${ITEM_TITLE} text, ${ITEM_DESCRIPTION} text, ${ITEM_DATE} number);"

        // // Uri
        // authority
        val AUTHORITY = "ru.barsopen.providers.listItems"

        // path
        val LIST_PATH = "listTable"

        // Общий Uri
        val LIST_CONTENT_URI = Uri.parse("content://${AUTHORITY}/${LIST_PATH}")

        // Типы данных
        // набор строк
        val LIST_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.${AUTHORITY}.${LIST_PATH}"

        // одна строка
        val LIST_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.${AUTHORITY}.${LIST_PATH}"

        //// UriMatcher
        // общий Uri
        val URI_LIST = 1

        // Uri с указанным ID
        val URI_LIST_ID = 2
    }

    // описание и создание UriMatcher
    private val uriMatcher: UriMatcher by lazy {
        val iMatcher = UriMatcher(UriMatcher.NO_MATCH)
        iMatcher.addURI(AUTHORITY, LIST_PATH, URI_LIST)
        iMatcher.addURI(AUTHORITY, LIST_PATH + "/#", URI_LIST_ID)
        iMatcher
    }

    var dbHelper: DBHelper? = null
    var db: SQLiteDatabase? = null

    override fun onCreate(): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun insert(p0: Uri?, p1: ContentValues?): Uri {
        return Uri.EMPTY
    }

    override fun query(p0: Uri?, p1: Array<out String>?, p2: String?, p3: Array<out String>?, p4: String?): Cursor {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun update(p0: Uri?, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(p0: Uri?, p1: String?, p2: Array<out String>?): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getType(p0: Uri?): String {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
