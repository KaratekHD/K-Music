package net.karatek.retromusic.adapter.song

import android.app.ActivityOptions
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import net.karatek.retromusic.R
import net.karatek.retromusic.adapter.base.AbsMultiSelectAdapter
import net.karatek.retromusic.adapter.base.MediaEntryViewHolder
import net.karatek.retromusic.extensions.hide
import net.karatek.retromusic.extensions.show
import net.karatek.retromusic.glide.RetroMusicColoredTarget
import net.karatek.retromusic.glide.SongGlideRequest
import net.karatek.retromusic.helper.MusicPlayerRemote
import net.karatek.retromusic.helper.SortOrder
import net.karatek.retromusic.helper.menu.SongMenuHelper
import net.karatek.retromusic.helper.menu.SongsMenuHelper
import net.karatek.retromusic.interfaces.CabHolder
import net.karatek.retromusic.model.Song
import net.karatek.retromusic.util.MusicUtil
import net.karatek.retromusic.util.NavigationUtil
import net.karatek.retromusic.util.PreferenceUtil
import com.afollestad.materialcab.MaterialCab
import com.bumptech.glide.Glide
import me.zhanghai.android.fastscroll.PopupTextProvider
import java.util.ArrayList

/**
 * Created by hemanths on 13/08/17.
 */

open class SongAdapter(
    protected val activity: AppCompatActivity,
    dataSet: ArrayList<Song>,
    protected var itemLayoutRes: Int,
    cabHolder: CabHolder?,
    showSectionName: Boolean = true
) : AbsMultiSelectAdapter<SongAdapter.ViewHolder, Song>(
    activity, cabHolder, R.menu.menu_media_selection
), MaterialCab.Callback, PopupTextProvider {

    var dataSet: ArrayList<Song>
    private var showSectionName = true

    init {
        this.dataSet = dataSet
        this.showSectionName = showSectionName
        this.setHasStableIds(true)
    }

    open fun swapDataSet(dataSet: ArrayList<Song>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return dataSet[position].id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false)
        return createViewHolder(view)
    }

    protected open fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = dataSet[position]
        val isChecked = isChecked(song)
        holder.itemView.isActivated = isChecked
        if (isChecked) {
            holder.menu?.hide()
        } else {
            holder.menu?.show()
        }
        holder.title?.text = getSongTitle(song)
        holder.text?.text = getSongText(song)
        loadAlbumCover(song, holder)
    }

    private fun setColors(color: Int, holder: ViewHolder) {
        if (holder.paletteColorContainer != null) {
            holder.paletteColorContainer?.setBackgroundColor(color)
            //holder.title?.setTextColor(MaterialValueHelper.getPrimaryTextColor(activity, ColorUtil.isColorLight(color)))
            //holder.text?.setTextColor(MaterialValueHelper.getSecondaryTextColor(activity, ColorUtil.isColorLight(color)))
        }
    }

    protected open fun loadAlbumCover(song: Song, holder: ViewHolder) {
        if (holder.image == null) {
            return
        }
        SongGlideRequest.Builder.from(Glide.with(activity), song).checkIgnoreMediaStore(activity)
            .generatePalette(activity).build()
            .into(object : RetroMusicColoredTarget(holder.image!!) {
                override fun onLoadCleared(placeholder: Drawable?) {
                    super.onLoadCleared(placeholder)
                    setColors(defaultFooterColor, holder)
                }

                override fun onColorReady(color: Int) {
                    setColors(color, holder)
                }
            })
    }

    private fun getSongTitle(song: Song): String? {
        return song.title
    }

    private fun getSongText(song: Song): String? {
        return song.artistName
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getIdentifier(position: Int): Song? {
        return dataSet[position]
    }

    override fun getName(song: Song): String {
        return song.title
    }

    override fun onMultipleItemAction(menuItem: MenuItem, selection: ArrayList<Song>) {
        SongsMenuHelper.handleMenuClick(activity, selection, menuItem.itemId)
    }

    override fun getPopupText(position: Int): String {
        val sectionName: String? = when (PreferenceUtil.getInstance(activity).songSortOrder) {
            SortOrder.SongSortOrder.SONG_A_Z, SortOrder.SongSortOrder.SONG_Z_A -> dataSet[position].title
            SortOrder.SongSortOrder.SONG_ALBUM -> dataSet[position].albumName
            SortOrder.SongSortOrder.SONG_ARTIST -> dataSet[position].artistName
            SortOrder.SongSortOrder.SONG_YEAR -> return MusicUtil.getYearString(dataSet[position].year)
            SortOrder.SongSortOrder.COMPOSER -> dataSet[position].composer
            else -> {
                return ""
            }
        }
        println("File name -> $sectionName")
        return MusicUtil.getSectionName(sectionName)
    }

    open inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {
        protected open var songMenuRes = SongMenuHelper.MENU_RES
        protected open val song: Song
            get() = dataSet[adapterPosition]

        init {
            setImageTransitionName(activity.getString(R.string.transition_album_art))
            menu?.setOnClickListener(object : SongMenuHelper.OnClickSongMenu(activity) {
                override val song: Song
                    get() = this@ViewHolder.song

                override val menuRes: Int
                    get() = songMenuRes

                override fun onMenuItemClick(item: MenuItem): Boolean {
                    return onSongMenuItemClick(item) || super.onMenuItemClick(item)
                }
            })
        }

        protected open fun onSongMenuItemClick(item: MenuItem): Boolean {
            if (image != null && image!!.visibility == View.VISIBLE) {
                when (item.itemId) {
                    R.id.action_go_to_album -> {
                        val activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                            activity,
                            imageContainerCard ?: image,
                            "${activity.getString(R.string.transition_album_art)}_${song.albumId}"
                        )
                        NavigationUtil.goToAlbumOptions(activity, song.albumId, activityOptions)
                        return true
                    }
                }
            }
            return false
        }

        override fun onClick(v: View?) {
            if (isInQuickSelectMode) {
                toggleChecked(adapterPosition)
            } else {
                MusicPlayerRemote.openQueue(dataSet, adapterPosition, true)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            return toggleChecked(adapterPosition)
        }
    }

    companion object {

        val TAG: String = SongAdapter::class.java.simpleName
    }
}
