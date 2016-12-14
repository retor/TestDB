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

class ListProvider : ContentProvider() {

    companion object {
        // Таблица
        val LIST_TABLE = "listTable"

        // Поля
        val ITEM_ID = "_id"
        val ITEM_TITLE = "title"
        val ITEM_DESCRIPTION = "desc"
        val ITEM_DATE = "date"

        // Скрипт создания таблицы
        val DB_CREATE = "create table $LIST_TABLE($ITEM_ID integer primary key autoincrement, " +
                "$ITEM_TITLE text, $ITEM_DESCRIPTION text, $ITEM_DATE number);"

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

    private val uriMatcher: UriMatcher by lazy {
        val iMatcher = UriMatcher(UriMatcher.NO_MATCH)
        iMatcher.addURI(AUTHORITY, LIST_PATH, URI_LIST)
        iMatcher.addURI(AUTHORITY, LIST_PATH + "/#", URI_LIST_ID)
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

    private val LOG_TAG: String = "ListProvider"

    override fun insert(p0: Uri?, p1: ContentValues?): Uri {
        Log.d(LOG_TAG, "insert, " + p0.toString())
        if (uriMatcher.match(p0) !== URI_LIST)
            throw IllegalArgumentException("Wrong URI: " + p0)

        val rowID = db.insert(LIST_TABLE, null, p1)
        val resultUri = ContentUris.withAppendedId(LIST_CONTENT_URI, rowID)
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        context.contentResolver.notifyChange(resultUri, null)
        return resultUri
    }

    override fun query(p0: Uri?, p1: Array<out String>?, p2: String?, p3: Array<out String>?, p4: String?): Cursor {
        var sortOrder = p4
        var selection = p2
        when (uriMatcher.match(p0)) {
            URI_LIST -> {
                Log.d(LOG_TAG, "URI_LIST")
                if (!sortOrder.isNullOrBlank() || sortOrder!!.isEmpty()) {
                    sortOrder = ITEM_ID/* + " ASC"*/
                }
            }
            URI_LIST_ID -> {
                val id = p0?.lastPathSegment
                Log.d(LOG_TAG, "URI_LIST_ID, " + id)
                if (TextUtils.isEmpty(selection)) {
                    selection = ITEM_ID + " = " + id
                } else {
                    selection = "$selection AND $ITEM_ID = $id"
                }
            }
            else -> throw IllegalArgumentException("Wrong URI: " + p0)
        }

        val cursor = db.query(LIST_TABLE, p1, selection,
                p3, null, null, sortOrder)

        cursor.setNotificationUri(context!!.contentResolver,
                LIST_CONTENT_URI)
        return cursor
    }

    override fun update(p0: Uri?, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        var selection = p2
        Log.d(LOG_TAG, "update, " + p0.toString())
        when (uriMatcher.match(p0)) {
            URI_LIST -> Log.d(LOG_TAG, "URI_CONTACTS")
            URI_LIST_ID -> {
                val id = p0?.lastPathSegment
                Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id)
                if (TextUtils.isEmpty(selection)) {
                    selection = ITEM_ID + " = " + id
                } else {
                    selection = "$selection AND $ITEM_ID = $id"
                }
            }
            else -> throw IllegalArgumentException("Wrong URI: " + p0)
        }

        val cnt = db.update(LIST_TABLE, p1, selection, p3)
        context.contentResolver.notifyChange(p0, null)
        return cnt
    }

    override fun delete(p0: Uri?, p1: String?, p2: Array<out String>?): Int {
        var selection = p1
        Log.d(LOG_TAG, "delete, " + p0.toString())
        when (uriMatcher.match(p0)) {
            URI_LIST -> Log.d(LOG_TAG, "URI_CONTACTS")
            URI_LIST_ID -> {
                val id = p0?.lastPathSegment
                Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id)
                if (TextUtils.isEmpty(selection)) {
                    selection = ITEM_ID + " = " + id
                } else {
                    selection = "$selection AND $ITEM_ID = $id"
                }
            }
            else -> throw IllegalArgumentException("Wrong URI: " + p0)
        }
        val cnt = db.delete(LIST_TABLE, selection, p2)
        context!!.contentResolver.notifyChange(p0, null)
        return cnt
    }

    override fun getType(p0: Uri?): String? {
        Log.d(LOG_TAG, "getType, " + p0.toString())
        when (uriMatcher.match(p0)) {
            URI_LIST -> return LIST_CONTENT_TYPE
            URI_LIST_ID -> return LIST_CONTENT_ITEM_TYPE
        }
        return null
    }

}
