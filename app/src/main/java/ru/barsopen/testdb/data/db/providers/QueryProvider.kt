package ru.barsopen.testdb.data.db.providers

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import ru.barsopen.testdb.data.db.DBHelper

/**
 * Created by d.sokolov on 13.12.2016.
 */
class QueryProvider : ContentProvider() {
    companion object {
        // Таблица
        val QUERY_TABLE = "listTable"

        // Поля
        val QUERY_ID = "_id"
        val QUERY_POSITION = "position"
        val QUERY_PREV = "previous"
        val QUERY_NEXT = "next"

        // Скрипт создания таблицы
        val DB_CREATE = "CREATE TABLE IF NOT EXISTS $QUERY_TABLE($QUERY_ID integer primary key autoincrement, " +
                "$QUERY_POSITION number, $QUERY_PREV number, $QUERY_NEXT number);"

        // // Uri
        // authority
        val AUTHORITY = "ru.barsopen.providers.query"

        // path
        val QUERY_PATH = "queryTable"

        // Общий Uri
        val QUERY_CONTENT_URI = Uri.parse("content://$AUTHORITY/$QUERY_PATH")

        // Типы данных
        // набор строк
        val QUERY_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$AUTHORITY.$QUERY_PATH"

        // одна строка
        val QUERY_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.$AUTHORITY.$QUERY_PATH"

        //// UriMatcher
        // общий Uri
        val URI_QUERY = 10

        // Uri с указанным ID
        val URI_QUERY_ID = 20
    }

    private val uriMatcher: UriMatcher by lazy {
        val iMatcher = UriMatcher(UriMatcher.NO_MATCH)
        iMatcher.addURI(AUTHORITY, QUERY_PATH, URI_QUERY)
        iMatcher.addURI(AUTHORITY, QUERY_PATH + "/#", URI_QUERY_ID)
        iMatcher
    }

    private val dbHelper: DBHelper by lazy {
        DBHelper(context)
    }

    private val db: SQLiteDatabase by lazy {
        dbHelper.writableDatabase
    }

    override fun onCreate(): Boolean {
        return true
    }

    private val LOG_TAG: String = "QueryProvider"

    override fun insert(p0: Uri?, p1: ContentValues?): Uri {
        Log.d(LOG_TAG, "insert, " + p0.toString())
        if (uriMatcher.match(p0) !== URI_QUERY)
            throw IllegalArgumentException("Wrong URI: " + p0)

        val rowID = db.insert(QUERY_TABLE, null, p1)
        val resultUri = ContentUris.withAppendedId(QUERY_CONTENT_URI, rowID)
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        context.contentResolver.notifyChange(resultUri, null)
        return resultUri
    }

    override fun query(p0: Uri?, p1: Array<out String>?, p2: String?, p3: Array<out String>?, p4: String?): Cursor {
        var sortOrder = p4
        var selection = p2
        when (uriMatcher.match(p0)) {
            URI_QUERY -> {
                Log.d(LOG_TAG, "URI_LIST")
                if (!sortOrder.isNullOrBlank() || sortOrder!!.isEmpty()) {
                    sortOrder = QUERY_ID/* + " ASC"*/
                }
            }
            URI_QUERY_ID -> {
                val id = p0?.lastPathSegment
                Log.d(LOG_TAG, "URI_LIST_ID, " + id)
                if (TextUtils.isEmpty(selection)) {
                    selection = QUERY_ID + " = " + id
                } else {
                    selection = "$selection AND $QUERY_ID = $id"
                }
            }
            else -> throw IllegalArgumentException("Wrong URI: " + p0)
        }

        val cursor = db.query(QUERY_TABLE, p1, selection,
                p3, null, null, sortOrder)

        cursor.setNotificationUri(context!!.contentResolver,
                QUERY_CONTENT_URI)
        return cursor
    }

    override fun update(p0: Uri?, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        var selection = p2
        Log.d(LOG_TAG, "update, " + p0.toString())
        when (uriMatcher.match(p0)) {
            URI_QUERY -> Log.d(LOG_TAG, "URI_CONTACTS")
            URI_QUERY_ID -> {
                val id = p0?.lastPathSegment
                Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id)
                if (TextUtils.isEmpty(selection)) {
                    selection = QUERY_ID + " = " + id
                } else {
                    selection = "$selection AND $QUERY_ID = $id"
                }
            }
            else -> throw IllegalArgumentException("Wrong URI: " + p0)
        }

        val cnt = db.update(QUERY_TABLE, p1, selection, p3)
        context.contentResolver.notifyChange(p0, null)
        return cnt
    }

    override fun delete(p0: Uri?, p1: String?, p2: Array<out String>?): Int {
        var selection = p1
        Log.d(LOG_TAG, "delete, " + p0.toString())
        when (uriMatcher.match(p0)) {
            URI_QUERY -> Log.d(LOG_TAG, "URI_CONTACTS")
            URI_QUERY_ID -> {
                val id = p0?.lastPathSegment
                Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id)
                if (TextUtils.isEmpty(selection)) {
                    selection = QUERY_ID + " = " + id
                } else {
                    selection = "$selection AND $QUERY_ID = $id"
                }
            }
            else -> throw IllegalArgumentException("Wrong URI: " + p0)
        }
        val cnt = db.delete(QUERY_TABLE, selection, p2)
        context!!.contentResolver.notifyChange(p0, null)
        return cnt
    }

    override fun getType(p0: Uri?): String? {
        Log.d(LOG_TAG, "getType, " + p0.toString())
        when (uriMatcher.match(p0)) {
            URI_QUERY -> return QUERY_CONTENT_TYPE
            URI_QUERY_ID -> return QUERY_CONTENT_ITEM_TYPE
        }
        return null
    }
}