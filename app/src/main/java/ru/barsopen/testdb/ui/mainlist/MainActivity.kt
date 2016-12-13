package ru.barsopen.testdb.ui.mainlist

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.barsopen.testdb.R
import ru.barsopen.testdb.domain.interactors.ItemsInteractor
import ru.barsopen.testdb.domain.models.Item
import ru.barsopen.testdb.rx_events.PositionChanged
import ru.barsopen.testdb.rx_events.RxBinder
import ru.barsopen.testdb.ui.adapters.SimpleAdapter
import rx.Observable
import rx.Subscription
import rx.android.MainThreadSubscription
import rx.functions.Func1
import java.util.*


class MainActivity : AppCompatActivity(), SimpleAdapter.DragListener {

    override fun onItemDragged(oldP: Int, newP: Int) {
        dragListener?.let {
            it.onItemDragged(oldP, newP)
        }
    }

    private val presenter by lazy {
        MainPresenter(ItemsInteractor())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        presenter.onAttached(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetached(this)
    }

    lateinit var mRecyclerViewDragDropManager: RecyclerViewDragDropManager
    var arrayList:MutableList<Item> = ArrayList<Item>()
    val myItemAdapter by lazy {
        SimpleAdapter(ArrayList())
    }

    var dragListener: SimpleAdapter.DragListener?=null

    private fun initRecycler(array:List<Item>) {
        if (array.isNotEmpty()) {
            arrayList = array as ArrayList<Item>
        }
        // drag & drop manager
        mRecyclerViewDragDropManager = RecyclerViewDragDropManager()
        // Start dragging after long press
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true)
        mRecyclerViewDragDropManager.setInitiateOnMove(false)

        myItemAdapter.outerListener = this
        myItemAdapter.setHasStableIds(true)

        myItemAdapter.items = arrayList

        val mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter)      // wrap for dragging

        val animator = DraggableItemAnimator()

        recycler.layoutManager = LinearLayoutManager(recycler.context)

        recycler.adapter = mWrappedAdapter

        recycler.itemAnimator = animator

        mRecyclerViewDragDropManager.attachRecyclerView(recycler)
    }

    fun onItemDragged(): Observable<PositionChanged> {
        return Observable.create {
            dragListener = object : SimpleAdapter.DragListener {
                override fun onItemDragged(oldP: Int, newP: Int) {
                    it?.let {
                        if (!it.isUnsubscribed)
                            it.onNext(PositionChanged(oldP, newP))
                    }
                }
            }
            it.add(object : MainThreadSubscription() {
                override fun onUnsubscribe() {
                    dragListener = null
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {
            it.putParcelableArrayList("a", arrayList as ArrayList<Item>)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.let {
            arrayList = it.getParcelableArrayList("a")
        }
    }

    override fun onPause() {
        mRecyclerViewDragDropManager.cancelDrag()
        super.onPause()
    }

    fun showError(message: String?) {
        AlertDialog.Builder(this).setTitle("Error").setMessage(message).show()
    }

    fun isRestored(): Observable<List<Item>> {
        return Observable.defer { Observable.just(arrayList as List<Item>) }
    }

    fun fillList(): Func1<Observable<List<Item>>, Subscription> {
       return RxBinder.ui({ initRecycler(it) }, { showError(it.message) })
    }
}
