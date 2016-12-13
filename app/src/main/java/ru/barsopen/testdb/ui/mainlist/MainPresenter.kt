package ru.barsopen.testdb.ui.mainlist

import ru.barsopen.testdb.domain.interactors.BaseItemsInteractor
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

    private operator fun CompositeSubscription.plusAssign(subscription: Subscription) = this.add(subscription)

    fun onAttached(view: MainActivity){
        val publish = view.isRestored().publish()

        subscription.add(RxBinder.bind(publish.filter { it.isEmpty() }
                .flatMap { interactor.getItems()},
                view.fillList()))

        subscription.add(RxBinder.bind(publish.filter { it.isNotEmpty() },
                view.fillList()))

        publish.connect()

        view.onItemDragged()
                .flatMap {
                    interactor.changePositions(it.from, it.to)
                            .filter { it!=null && it.isNotEmpty() }
                }
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnError { view?.let{v-> v.showError(it.message) }}
                .toSingle()
                .subscribe()

    }

    fun onDetached(view: MainActivity){
        if (subscription.hasSubscriptions())
            subscription.clear()
    }
}