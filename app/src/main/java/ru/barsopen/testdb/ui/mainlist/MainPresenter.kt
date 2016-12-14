package ru.barsopen.testdb.ui.mainlist

import android.util.Log
import ru.barsopen.testdb.domain.interactors.BaseItemsInteractor
import ru.barsopen.testdb.rx_events.PositionChanged
import ru.barsopen.testdb.rx_events.RxBinder
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

/**
 * Created by d.sokolov on 13.12.2016.
 */
class MainPresenter constructor(val interactor: BaseItemsInteractor){
    private val subscription: CompositeSubscription = CompositeSubscription()
    private var lastChange:PositionChanged?=null

    private operator fun CompositeSubscription.plusAssign(subscription: Subscription) = this.add(subscription)

    fun onAttached(view: MainActivity){
        val publish = view.isRestored().publish()

        subscription.add(RxBinder.bind(publish.filter { it.isEmpty() }
                .flatMap { interactor.getItems()},
                view.fillList()))

        subscription.add(RxBinder.bind(publish.filter { it.isNotEmpty() },
                view.fillList()))

        publish.connect()

        subscription.add(RxBinder.bind(view.onItemDragged()
                .doOnNext { lastChange = it }
                .flatMap {
                    interactor.changePositions(it.from, it.to)
                            .filter { it!=null && it.isNotEmpty() }
                            .doOnNext { lastChange=null }
                }
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnError { view?.showError(it.message)
                    Log.e("End", it.message)}, view.draggedSuccess()))
    }

    fun onDetached(view: MainActivity){
        lastChange?.let { view.startSaveServiceI(it)}
        if (subscription.hasSubscriptions())
            subscription.clear()
    }
}