package net.karatek.retromusic.fragments.mainactivity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import net.karatek.retromusic.App
import net.karatek.retromusic.R
import net.karatek.retromusic.adapter.artist.ArtistAdapter
import net.karatek.retromusic.fragments.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment
import net.karatek.retromusic.model.Artist
import net.karatek.retromusic.mvp.presenter.ArtistsPresenter
import net.karatek.retromusic.mvp.presenter.ArtistsView
import net.karatek.retromusic.util.PreferenceUtil
import javax.inject.Inject

class ArtistsFragment : AbsLibraryPagerRecyclerViewCustomGridSizeFragment<ArtistAdapter, GridLayoutManager>(),
    ArtistsView {

    override fun artists(artists: ArrayList<Artist>) {
        adapter?.swapDataSet(artists)
    }

    @Inject
    lateinit var artistsPresenter: ArtistsPresenter

    override val emptyMessage: Int
        get() = R.string.no_artists

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.musicComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        artistsPresenter.attachView(this)
    }

    override fun onResume() {
        super.onResume()
        if (adapter!!.dataSet.isEmpty()) {
            artistsPresenter.loadArtists()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        artistsPresenter.detachView()
    }

    override fun onMediaStoreChanged() {
        artistsPresenter.loadArtists()
    }

    override fun setSortOrder(sortOrder: String) {
        artistsPresenter.loadArtists()
    }

    override fun createLayoutManager(): GridLayoutManager {
        return GridLayoutManager(activity, getGridSize())
    }

    override fun createAdapter(): ArtistAdapter {
        val dataSet = if (adapter == null) ArrayList() else adapter!!.dataSet
        return ArtistAdapter(
            libraryFragment.mainActivity,
            dataSet,
            itemLayoutRes(),
            loadUsePalette(),
            libraryFragment
        )
    }

    override fun loadGridSize(): Int {
        return PreferenceUtil.getInstance(requireContext()).getArtistGridSize(activity!!)
    }

    override fun saveGridSize(gridColumns: Int) {
        PreferenceUtil.getInstance(requireContext()).setArtistGridSize(gridColumns)
    }

    override fun loadGridSizeLand(): Int {
        return PreferenceUtil.getInstance(requireContext()).getArtistGridSizeLand(activity!!)
    }

    override fun saveGridSizeLand(gridColumns: Int) {
        PreferenceUtil.getInstance(requireContext()).setArtistGridSizeLand(gridColumns)
    }

    override fun saveUsePalette(usePalette: Boolean) {
        PreferenceUtil.getInstance(requireContext()).setArtistColoredFooters(usePalette)
    }

    public override fun loadUsePalette(): Boolean {
        return PreferenceUtil.getInstance(requireContext()).artistColoredFooters()
    }

    override fun setUsePalette(usePalette: Boolean) {
        adapter?.usePalette(usePalette)
    }

    override fun setGridSize(gridSize: Int) {
        layoutManager?.spanCount = gridSize
        adapter?.notifyDataSetChanged()
    }

    override fun loadSortOrder(): String {
        return PreferenceUtil.getInstance(requireContext()).artistSortOrder
    }

    override fun saveSortOrder(sortOrder: String) {
        PreferenceUtil.getInstance(requireContext()).artistSortOrder = sortOrder
    }

    override fun showEmptyView() {
        adapter?.swapDataSet(ArrayList())
    }

    companion object {
        @JvmField
        val TAG: String = ArtistsFragment::class.java.simpleName

        fun newInstance(): ArtistsFragment {

            val args = Bundle()

            val fragment = ArtistsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun setLayoutRes(layoutRes: Int) {
    }

    override fun loadLayoutRes(): Int {
        return PreferenceUtil.getInstance(requireContext()).artistGridStyle
    }

    override fun saveLayoutRes(layoutRes: Int) {
        PreferenceUtil.getInstance(requireContext()).artistGridStyle = layoutRes
    }
}