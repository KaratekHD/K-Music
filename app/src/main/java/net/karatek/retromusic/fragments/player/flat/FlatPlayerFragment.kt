package net.karatek.retromusic.fragments.player.flat

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import code.name.monkey.appthemehelper.util.ATHUtil
import code.name.monkey.appthemehelper.util.ColorUtil
import code.name.monkey.appthemehelper.util.MaterialValueHelper
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import net.karatek.retromusic.R
import net.karatek.retromusic.fragments.base.AbsPlayerFragment
import net.karatek.retromusic.fragments.player.PlayerAlbumCoverFragment
import net.karatek.retromusic.helper.MusicPlayerRemote
import net.karatek.retromusic.model.Song
import net.karatek.retromusic.util.PreferenceUtil
import net.karatek.retromusic.util.ViewUtil
import net.karatek.retromusic.views.DrawableGradient
import kotlinx.android.synthetic.main.fragment_flat_player.colorGradientBackground
import kotlinx.android.synthetic.main.fragment_flat_player.playerToolbar

class FlatPlayerFragment : AbsPlayerFragment() {
    override fun playerToolbar(): Toolbar {
        return playerToolbar
    }

    private var valueAnimator: ValueAnimator? = null
    private lateinit var flatPlaybackControlsFragment: FlatPlaybackControlsFragment
    private var lastColor: Int = 0
    override val paletteColor: Int
        get() = lastColor

    private fun setUpSubFragments() {
        flatPlaybackControlsFragment =
            childFragmentManager.findFragmentById(R.id.playbackControlsFragment) as FlatPlaybackControlsFragment
        val playerAlbumCoverFragment =
            childFragmentManager.findFragmentById(R.id.playerAlbumCoverFragment) as PlayerAlbumCoverFragment
        playerAlbumCoverFragment.setCallbacks(this)
    }

    private fun setUpPlayerToolbar() {
        playerToolbar.inflateMenu(R.menu.menu_player)
        playerToolbar.setNavigationOnClickListener { _ -> requireActivity().onBackPressed() }
        playerToolbar.setOnMenuItemClickListener(this)
        ToolbarContentTintHelper.colorizeToolbar(
            playerToolbar,
            ATHUtil.resolveColor(requireContext(), R.attr.colorControlNormal),
            requireActivity()
        )
    }

    private fun colorize(i: Int) {
        if (valueAnimator != null) {
            valueAnimator!!.cancel()
        }

        valueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), android.R.color.transparent, i)
        valueAnimator!!.addUpdateListener { animation ->
            val drawable = DrawableGradient(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(animation.animatedValue as Int, android.R.color.transparent), 0
            )
            colorGradientBackground?.background = drawable

        }
        valueAnimator!!.setDuration(ViewUtil.RETRO_MUSIC_ANIM_TIME.toLong()).start()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_flat_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPlayerToolbar()
        setUpSubFragments()
    }

    override fun onShow() {
        flatPlaybackControlsFragment.show()
    }

    override fun onHide() {
        flatPlaybackControlsFragment.hide()
        onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun toolbarIconColor(): Int {
        val isLight = ColorUtil.isColorLight(paletteColor)
        return if (PreferenceUtil.getInstance(requireContext()).adaptiveColor)
            MaterialValueHelper.getPrimaryTextColor(requireContext(), isLight)
        else
            ATHUtil.resolveColor(context, R.attr.colorControlNormal)
    }

    override fun onColorChanged(color: Int) {
        lastColor = color
        flatPlaybackControlsFragment.setDark(color)
        callbacks?.onPaletteColorChanged()
        val isLight = ColorUtil.isColorLight(color)
        val iconColor = if (PreferenceUtil.getInstance(requireContext()).adaptiveColor)
            MaterialValueHelper.getPrimaryTextColor(requireContext(), isLight)
        else
            ATHUtil.resolveColor(requireContext(), R.attr.colorControlNormal)
        ToolbarContentTintHelper.colorizeToolbar(playerToolbar, iconColor, requireActivity())
        if (PreferenceUtil.getInstance(requireContext()).adaptiveColor) {
            colorize(color)
        }
    }

    override fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
    }

    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }
}