package ru.barsopen.testdb.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import rx.Observable
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit

/**
 * Created by d.sokolov on 14.12.2016.
 */
class SaveService : Service() {
    val subscription: CompositeSubscription = CompositeSubscription()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            subscription.add(Observable.timer(3, TimeUnit.SECONDS)
                    .doOnSubscribe { Log.d("End", "started") }
                    .doOnUnsubscribe { Log.d("End", "unsubscribed") }
                    .doOnTerminate { Log.d("End", "terminated") }
                    .map { it.toInt() }
                    .toSingle()
                    .subscribe({
                        Log.d("End", "finishing $it")
                        Log.d("End", Thread.currentThread().name)
                        stopSelf()
                    }, { Log.e("End", it.message) }))
        }
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        if (subscription.hasSubscriptions() && !subscription.isUnsubscribed)
            subscription.clear()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {

        return null
    }


}