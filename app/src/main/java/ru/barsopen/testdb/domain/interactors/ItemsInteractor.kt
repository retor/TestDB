package ru.barsopen.testdb.domain.interactors

import ru.barsopen.testdb.domain.models.Item
import rx.Observable
import java.util.*

/**
 * Created by d.sokolov on 13.12.2016.
 */
class ItemsInteractor constructor():BaseItemsInteractor {

    override fun getItems(): Observable<List<Item>> {
        return Observable.just(ArrayList<Item>(arrayListOf(
                Item("1a", "aaa", System.currentTimeMillis()),
                Item("2b", "bbb", System.currentTimeMillis()),
                Item("3c", "ccc", System.currentTimeMillis()),
                Item("4d", "ddd", System.currentTimeMillis()),
                Item("5e", "eee", System.currentTimeMillis())
        )))
    }

    override fun removeItem(item: Item): Observable<Boolean> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addItem(item: Item): Observable<Boolean> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changePositions(old: Int, last: Int): Observable<List<Item>> {
        return Observable.empty<List<Item>>()
    }
}