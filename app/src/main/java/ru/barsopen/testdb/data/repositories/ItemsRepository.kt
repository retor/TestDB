package ru.barsopen.testdb.data.repositories

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import ru.barsopen.testdb.data.db.models.ItemDB
import ru.barsopen.testdb.data.db.providers.ListProvider
import rx.Observable
import java.util.*

/**
 * Created by d.sokolov on 13.12.2016.
 */
class ItemsRepository constructor(val resolver: ContentResolver) {

    fun getItems(): Observable<List<ItemDB>> {
        return Observable.just(resolver.query(ListProvider.LIST_CONTENT_URI, null, null, null, null))
                .flatMap { c ->
                    val out: MutableList<ItemDB> = ArrayList()
                    if (c.moveToFirst()) {
                        while (!c.isAfterLast) {
                            val id = c.getInt(c.getColumnIndex("_id"))
                            val title = c.getString(c.getColumnIndex("title"))
                            val desc = c.getString(c.getColumnIndex("desc"))
                            val date = c.getLong(c.getColumnIndex("date"))
                            out.add(ItemDB(id, title, desc, date))
                            c.moveToNext()
                        }
                    }
                    c.close()
                    Observable.just(out)
                }
    }

    fun getItem(id: Int): Observable<ItemDB> {
        return Observable.just(resolver.query(Uri.parse(ListProvider.LIST_CONTENT_URI.toString() + "/$id"), null, null, null, null))
                .flatMap { c ->
                    var out: ItemDB? = null
                    if (c.moveToFirst()) {
                        if (!c.isAfterLast) {
                            val id = c.getInt(c.getColumnIndex("_id"))
                            val title = c.getString(c.getColumnIndex("title"))
                            val desc = c.getString(c.getColumnIndex("desc"))
                            val date = c.getLong(c.getColumnIndex("date"))
                            out = ItemDB(id, title, desc, date)
                        }
                    }
                    c.close()
                    Observable.just(out)
                }
    }

    fun addItem(item: ItemDB): Observable<Boolean> {
        return Observable.just(item)
                .flatMap {
                    val content = ContentValues()
                    content.put("title", it.title)
                    content.put("desc", it.desc)
                    content.put("date", it.date)
                    resolver.insert(ListProvider.LIST_CONTENT_URI, content)
                    Observable.just(true)
                }
    }

    fun removeItem(item: ItemDB): Observable<Boolean> {
        return Observable.just(item)
                .flatMap {
                    val i = resolver.delete(Uri.parse(ListProvider.LIST_CONTENT_URI.toString() + "/${it.id}"), null, null)
                    Observable.just(i>0)
                }
    }

}