package net.karatek.retromusic.fragments.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import net.karatek.retromusic.R
import net.karatek.retromusic.adapter.album.AlbumCoverPagerAdapter
import net.karatek.retromusic.adapter.album.AlbumCoverPagerAdapter.AlbumCoverFragment
import net.karatek.retromusic.fragments.NowPlayingScreen
import net.karatek.retromusic.fragments.base.AbsMusicServiceFragment
import net.karatek.retromusic.helper.MusicPlayerRemote
import net.karatek.retromusic.transform.CarousalPagerTransformer
import net.karatek.retromusic.transform.ParallaxPagerTransformer
import net.karatek.retromusic.util.PreferenceUtil
import kotlinx.android.synthetic.main.fragment_player_album_cover.viewPager

class PlayerAlbumCoverFragment : AbsMusicServiceFragment(), ViewPager.OnPageChangeListener {
    private var callbacks: Callbacks? = null
    private var currentPosition: Int = 0
    private val colorReceiver = object : AlbumCoverFragment.ColorReceiver {
        override fun onColorReady(color: Int, request: Int) {
            if (currentPosition == request) {
                notifyColorChange(color)
            }
        }
    }

    fun removeSlideEffect() {
        val transformer = ParallaxPagerTransformer(R.id.player_image)
        transformer.setSpeed(0.3f)
        viewPager.setPageTransformer(true, transformer)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player_album_cover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.addOnPageChangeListener(this)
        val nowPlayingScreen = PreferenceUtil.getInstance(requireContext()).nowPlayingScreen

        if (PreferenceUtil.getInstance(requireContext()).carouselEffect() &&
            !((nowPlayingScreen == NowPlayingScreen.FULL) || (nowPlayingScreen == NowPlayingScreen.ADAPTIVE)
                    || (nowPlayingScreen == NowPlayingScreen.FIT))
        ) {
            viewPager.clipToPadding = false
            viewPager.setPadding(40, 40, 40, 0)
            viewPager.pageMargin = 0
            viewPager.setPageTransformer(false, CarousalPagerTransformer(requireContext()))
        } else {
            viewPager.offscreenPageLimit = 2
            viewPager.setPageTransformer(true, PreferenceUtil.getInstance(requireContext()).albumCoverTransform)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager.removeOnPageChangeListener(this)
    }

    override fun onServiceConnected() {
        updatePlayingQueue()
    }

    override fun onPlayingMetaChanged() {
        viewPager.currentItem = MusicPlayerRemote.position
    }

    override fun onQueueChanged() {
        updatePlayingQueue()
    }

    private fun updatePlayingQueue() {
        viewPager.apply {
            adapter = AlbumCoverPagerAdapter(childFragmentManager, MusicPlayerRemote.playingQueue)
            viewPager.adapter!!.notifyDataSetChanged()
            viewPager.currentItem = MusicPlayerRemote.position
            onPageSelected(MusicPlayerRemote.position)
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        currentPosition = position
        if (viewPager.adapter != null) {
            (viewPager.adapter as AlbumCoverPagerAdapter).receiveColor(colorReceiver, position)
        }
        if (position != MusicPlayerRemote.position) {
            MusicPlayerRemote.playSongAt(position)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    private fun notifyColorChange(color: Int) {
        callbacks?.onColorChanged(color)
    }

    fun setCallbacks(listener: Callbacks) {
        callbacks = listener
    }

    fun removeEffect() {
        viewPager.setPageTransformer(false, null)
    }

    interface Callbacks {

        fun onColorChanged(color: Int)

        fun onFavoriteToggled()
    }

    companion object {
        val TAG: String = PlayerAlbumCoverFragment::class.java.simpleName
    }
}
