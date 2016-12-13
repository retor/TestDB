package ru.barsopen.testdb.ui.adapters

import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder
import ru.barsopen.testdb.R
import ru.barsopen.testdb.domain.models.Item

/**
 * Created by d.sokolov on 13.12.2016.
 */
class SimpleAdapter(var items: MutableList<Item>) : RecyclerView.Adapter<SimpleAdapter.Companion.SimpleHolder>(), DraggableItemAdapter<SimpleAdapter.Companion.SimpleHolder> {
companion object{
    public class SimpleHolder constructor(val view:View) : AbstractDraggableItemViewHolder(view)
}

    public interface DragListener {
        fun onItemDragged(oldP: Int, newP: Int)
    }

    var outerListener: DragListener? = null

    fun addItems(items: List<Item>) {
        this.items.addAll(items)
    }

    fun addItem(item: Item) {
        this.items.add(item)
    }

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SimpleAdapter.Companion.SimpleHolder?, position: Int) {
        (holder?.itemView?.findViewById(R.id.itemTitle) as AppCompatTextView).text = items[position].title
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SimpleAdapter.Companion.SimpleHolder {
        return SimpleHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_holder, parent, false))
    }

    override fun onCheckCanStartDrag(holder: SimpleAdapter.Companion.SimpleHolder?, position: Int, x: Int, y: Int): Boolean {
        val containerView = holder?.itemView?.findViewById(R.id.con)
        val dragHandleView = holder?.itemView?.findViewById(R.id.itemTitle)
        val left = containerView?.left
        val top = containerView?.top
        val offsetX = left!! + (ViewCompat.getTranslationX(containerView) + 0.5f).toInt()
        val offsetY = (top!! + (ViewCompat.getTranslationY(containerView) + 0.5f).toInt())

        return hitTest(dragHandleView!!, x - offsetX, y - offsetY)
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) {
            return
        }
        val item = items[fromPosition]
        items.removeAt(fromPosition)
        items.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
        outerListener?.let {
            it.onItemDragged(fromPosition,toPosition)
        }
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean = true

    override fun onGetItemDraggableRange(holder: SimpleAdapter.Companion.SimpleHolder?, position: Int): ItemDraggableRange? = null

    private fun hitTest(v: View, x: Int, y: Int): Boolean {
        val tx = (ViewCompat.getTranslationX(v) + 0.5f).toInt()
        val ty = (ViewCompat.getTranslationY(v) + 0.5f).toInt()
        val left = v.left + tx
        val right = v.right + tx
        val top = v.top + ty
        val bottom = v.bottom + ty

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom)
    }

}