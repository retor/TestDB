package ru.barsopen.testdb.domain.interactors

import ru.barsopen.testdb.data.db.models.ItemDB
import ru.barsopen.testdb.data.repositories.ItemsRepository
import ru.barsopen.testdb.domain.models.Item
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by d.sokolov on 13.12.2016.
 */
class ItemsInteractor constructor(/*val queryRepository: QueryRepository,*/ val dataRepository: ItemsRepository) : BaseItemsInteractor {

    private fun exampleArray(): List<Item> = ArrayList<Item>(arrayListOf(
            Item(0, "1a", "aaa", System.currentTimeMillis()),
            Item(1, "2b", "bbb", System.currentTimeMillis()),
            Item(2, "3c", "ccc", System.currentTimeMillis()),
            Item(3, "4d", "ddd", System.currentTimeMillis()),
            Item(4, "5e", "eee", System.currentTimeMillis())
    ))

    override fun getItems(): Observable<List<Item>> {
        return dataRepository.getItems()
                .flatMap {
                    if (it.isEmpty())
                        Observable.just(exampleArray())
                                .flatMap {
                                    Observable.from(it)
                                            .map { e -> ItemDB(e.id, e.title, e.desc, e.date) }
                                            .flatMap { e -> dataRepository.addItem(e) }.toList()
                                            .map { b -> it }
                                }
                    else
                        dataRepository.getItems()
                                .flatMap {
                                    Observable.from(it)
                                            .map { e -> Item(e.id, e.title, e.desc, e.date) }
                                            .toList()
                                }
                }.observeOn(Schedulers.io())
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