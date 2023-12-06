package com.lizongying.mytv

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.ListRowPresenter.SelectItemViewHolderTask
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import androidx.lifecycle.lifecycleScope

class MainFragment : BrowseSupportFragment() {

    var itemPosition: Int = 0

    private val list2: MutableList<Info> = mutableListOf()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onActivityCreated(savedInstanceState)

        setupUIElements()
        loadRows()
        setupEventListeners()
    }

    private fun setupUIElements() {
        brandColor = ContextCompat.getColor(context!!, R.color.fastlane_background)
//        headersState = HEADERS_DISABLED
    }

    private var count: Int = 0

    private fun loadRows() {
        val list = TVList.list
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter(lifecycleScope)

        var idx: Long = 0
        for ((k, v) in list) {
            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
            var idx2 = 0
            for ((k1, v1) in v) {
                listRowAdapter.add(v1)
                list2.add(
                    Info(
                        idx.toInt(), idx2, TV(
                            count,
                            k1,
                            v1.toList()
                        )
                    )
                )
                count++
                idx2++
            }
            val header = HeaderItem(idx, k)
            rowsAdapter.add(ListRow(header, listRowAdapter))
            idx++
        }

        adapter = rowsAdapter

        (activity as? MainActivity)?.play(list2.first().item as TV)
        (activity as? MainActivity)?.switchMainFragment()
    }

    fun focus() {
        if (!view?.isFocused!!) {
            view?.requestFocus()
        }
    }

    fun prev() {
        view?.post {
            itemPosition--
            if (itemPosition == -1) {
                itemPosition = list2.size - 1
            }

            val l = list2[itemPosition]
            l.item?.let { (activity as? MainActivity)?.play(it) }
            setSelectedPosition(
                l.rowPosition, false,
                SelectItemViewHolderTask(l.itemPosition)
            )
//            Toast.makeText(
//                activity,
//                "${l.title} ${tv.videoIndex}",
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }

    fun next() {
        view?.post {
            itemPosition++
            if (itemPosition == list2.size) {
                itemPosition = 0
            }

            val l = list2[itemPosition]
            l.item?.let { (activity as? MainActivity)?.play(it) }
            setSelectedPosition(
                l.rowPosition, false,
                SelectItemViewHolderTask(l.itemPosition)
            )
//            Toast.makeText(
//                activity,
//                "${l.title} ${tv.videoIndex}",
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }

    fun prevSource() {
        view?.post {
            val item = list2[itemPosition]
            val tv = item.item as TV

            tv.videoIndex--
            if (tv.videoIndex == -1) {
                tv.videoIndex = tv.videoUrl.size - 1
            }

            (activity as? MainActivity)?.play(tv)
//            Toast.makeText(
//                activity,
//                "${l.title} ${tv.videoIndex}",
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }

    fun nextSource() {
        view?.post {
            val item = list2[itemPosition]
            val tv = item.item as TV

            tv.videoIndex++
            if (tv.videoIndex == tv.videoUrl.size) {
                tv.videoIndex = 0
            }

            (activity as? MainActivity)?.play(tv)
//            Toast.makeText(
//                activity,
//                "${l.title} ${tv.videoIndex}",
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            Log.d(TAG, "onItemClicked")
            if (item is TV) {
                Log.d(TAG, "Item: $item")
                (activity as? MainActivity)?.play(item)
                (activity as? MainActivity)?.switchMainFragment()
                itemPosition = item.id
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder, row: Row
        ) {
            if (item is TV) {
                Log.i(TAG, "Item: ${item.id}")
            }
            if (itemViewHolder == null) {
                view?.post {
                    val l = list2[itemPosition]
                    Log.i(TAG, "$l")
                    setSelectedPosition(
                        l.rowPosition, false,
                        SelectItemViewHolderTask(l.itemPosition)
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainFragment"
    }
}