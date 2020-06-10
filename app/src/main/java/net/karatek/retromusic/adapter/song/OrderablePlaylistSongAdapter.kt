package net.karatek.retromusic.adapter.song

import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import net.karatek.retromusic.R
import net.karatek.retromusic.R.menu
import net.karatek.retromusic.dialogs.RemoveFromPlaylistDialog
import net.karatek.retromusic.interfaces.CabHolder
import net.karatek.retromusic.model.PlaylistSong
import net.karatek.retromusic.model.Song
import net.karatek.retromusic.util.ViewUtil
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.draggable.annotation.DraggableItemStateFlags

class OrderablePlaylistSongAdapter(
    activity: AppCompatActivity,
    dataSet: ArrayList<Song>,
    itemLayoutRes: Int,
    cabHolder: CabHolder?,
    private val onMoveItemListener: OnMoveItemListener?
) : PlaylistSongAdapter(
    activity, dataSet, itemLayoutRes, cabHolder
), DraggableItemAdapter<OrderablePlaylistSongAdapter.ViewHolder> {

    init {
        setMultiSelectMenuRes(menu.menu_playlists_songs_selection)
    }

    override fun createViewHolder(view: View): SongAdapter.ViewHolder {
        return ViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        var positionFinal = position
        positionFinal--

        var long: Long = 0
        if (positionFinal < 0) {
            long = -2
        } else {
            if (dataSet[positionFinal] is PlaylistSong) {
                long = (dataSet[positionFinal] as PlaylistSong).idInPlayList.toLong()
            }
        }
        return long
    }

    override fun onMultipleItemAction(menuItem: MenuItem, selection: ArrayList<Song>) {
        when (menuItem.itemId) {
            R.id.action_remove_from_playlist -> {
                RemoveFromPlaylistDialog.create(selection as ArrayList<PlaylistSong>)
                    .show(activity.supportFragmentManager, "ADD_PLAYLIST")
                return
            }
        }
        super.onMultipleItemAction(menuItem, selection)
    }

    override fun onCheckCanStartDrag(holder: ViewHolder, position: Int, x: Int, y: Int): Boolean {
        return onMoveItemListener != null && position > 0 && (ViewUtil.hitTest(
            holder.dragView!!, x, y
        ) || ViewUtil.hitTest(holder.image!!, x, y))
    }

    override fun onGetItemDraggableRange(holder: ViewHolder, position: Int): ItemDraggableRange {
        return ItemDraggableRange(1, dataSet.size)
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        if (onMoveItemListener != null && fromPosition != toPosition) {
            onMoveItemListener.onMoveItem(fromPosition - 1, toPosition - 1)
        }
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
        return dropPosition > 0
    }

    override fun onItemDragStarted(position: Int) {
        notifyDataSetChanged()
    }

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
        notifyDataSetChanged()
    }

    interface OnMoveItemListener {
        fun onMoveItem(fromPosition: Int, toPosition: Int)
    }

    inner class ViewHolder(itemView: View) : PlaylistSongAdapter.ViewHolder(itemView), DraggableItemViewHolder {
        @DraggableItemStateFlags
        private var mDragStateFlags: Int = 0

        override var songMenuRes: Int
            get() = net.karatek.retromusic.R.menu.menu_item_playlist_song
            set(value) {
                super.songMenuRes = value
            }

        init {
            if (dragView != null) {
                if (onMoveItemListener != null) {
                    dragView?.visibility = View.VISIBLE
                } else {
                    dragView?.visibility = View.GONE
                }
            }
        }

        override fun onSongMenuItemClick(item: MenuItem): Boolean {
            when (item.itemId) {
                net.karatek.retromusic.R.id.action_remove_from_playlist -> {
                    RemoveFromPlaylistDialog.create(song as PlaylistSong)
                        .show(activity.supportFragmentManager, "REMOVE_FROM_PLAYLIST")
                    return true
                }
            }
            return super.onSongMenuItemClick(item)
        }

        @DraggableItemStateFlags
        override fun getDragStateFlags(): Int {
            return mDragStateFlags
        }

        override fun setDragStateFlags(@DraggableItemStateFlags flags: Int) {
            mDragStateFlags = flags
        }
    }

    companion object {
        val TAG: String = OrderablePlaylistSongAdapter::class.java.simpleName
    }
}
