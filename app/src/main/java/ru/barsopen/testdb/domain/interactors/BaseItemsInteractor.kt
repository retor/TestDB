package ru.barsopen.testdb.domain.interactors

import ru.barsopen.testdb.domain.models.Item
import rx.Observable

/**
 * Created by d.sokolov on 13.12.2016.
 */
interface BaseItemsInteractor {
    fun getItems():Observable<List<Item>>
    fun removeItem(item:Item):Observable<Boolean>
    fun addItem(item: Item):Observable<Boolean>
    fun changePositions(old:Int, last:Int):Observable<List<Item>>
}