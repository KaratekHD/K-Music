package net.karatek.retromusic.adapter.song

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import net.karatek.retromusic.R
import net.karatek.retromusic.helper.MusicPlayerRemote
import net.karatek.retromusic.interfaces.CabHolder
import net.karatek.retromusic.model.Song
import com.google.android.material.button.MaterialButton
import java.util.ArrayList

class ShuffleButtonSongAdapter(
    activity: AppCompatActivity,
    dataSet: ArrayList<Song>,
    itemLayoutRes: Int,
    cabHolder: CabHolder?
) : AbsOffsetSongAdapter(activity, dataSet, itemLayoutRes, cabHolder) {

    override fun createViewHolder(view: View): SongAdapter.ViewHolder {
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongAdapter.ViewHolder, position: Int) {
        if (holder.itemViewType == OFFSET_ITEM) {
            val viewHolder = holder as ViewHolder
            viewHolder.playAction?.let {
                it.setOnClickListener {
                    MusicPlayerRemote.openQueue(dataSet, 0, true)
                }
            }
            viewHolder.shuffleAction?.let {
                it.setOnClickListener {
                    MusicPlayerRemote.openAndShuffleQueue(dataSet, true)
                }
            }
        } else {
            super.onBindViewHolder(holder, position - 1)
        }
    }

    inner class ViewHolder(itemView: View) : AbsOffsetSongAdapter.ViewHolder(itemView) {
        val playAction: MaterialButton? = itemView.findViewById(R.id.playAction)
        val shuffleAction: MaterialButton? = itemView.findViewById(R.id.shuffleAction)

        override fun onClick(v: View?) {
            if (itemViewType == OFFSET_ITEM) {
                MusicPlayerRemote.openAndShuffleQueue(dataSet, true)
                return
            }
            super.onClick(v)
        }
    }
}