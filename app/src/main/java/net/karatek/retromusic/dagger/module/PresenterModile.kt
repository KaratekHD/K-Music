/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package net.karatek.retromusic.dagger.module

import android.content.Context
import net.karatek.retromusic.mvp.presenter.AlbumDetailsPresenter
import net.karatek.retromusic.mvp.presenter.AlbumDetailsPresenter.AlbumDetailsPresenterImpl
import net.karatek.retromusic.mvp.presenter.AlbumsPresenter
import net.karatek.retromusic.mvp.presenter.AlbumsPresenter.AlbumsPresenterImpl
import net.karatek.retromusic.mvp.presenter.ArtistDetailsPresenter
import net.karatek.retromusic.mvp.presenter.ArtistDetailsPresenter.ArtistDetailsPresenterImpl
import net.karatek.retromusic.mvp.presenter.ArtistsPresenter
import net.karatek.retromusic.mvp.presenter.ArtistsPresenter.ArtistsPresenterImpl
import net.karatek.retromusic.mvp.presenter.GenreDetailsPresenter
import net.karatek.retromusic.mvp.presenter.GenreDetailsPresenter.GenreDetailsPresenterImpl
import net.karatek.retromusic.mvp.presenter.GenresPresenter
import net.karatek.retromusic.mvp.presenter.GenresPresenter.GenresPresenterImpl
import net.karatek.retromusic.mvp.presenter.HomePresenter
import net.karatek.retromusic.mvp.presenter.HomePresenter.HomePresenterImpl
import net.karatek.retromusic.mvp.presenter.PlaylistSongsPresenter
import net.karatek.retromusic.mvp.presenter.PlaylistSongsPresenter.PlaylistSongsPresenterImpl
import net.karatek.retromusic.mvp.presenter.PlaylistsPresenter
import net.karatek.retromusic.mvp.presenter.PlaylistsPresenter.PlaylistsPresenterImpl
import net.karatek.retromusic.mvp.presenter.SearchPresenter
import net.karatek.retromusic.mvp.presenter.SearchPresenter.SearchPresenterImpl
import net.karatek.retromusic.mvp.presenter.SongPresenter
import net.karatek.retromusic.mvp.presenter.SongPresenter.SongPresenterImpl
import net.karatek.retromusic.providers.RepositoryImpl
import net.karatek.retromusic.providers.interfaces.Repository
import dagger.Module
import dagger.Provides

/**
 * Created by hemanths on 2019-12-30.
 */

@Module
class PresenterModule {

    @Provides
    fun providesRepository(context: Context): Repository {
        return RepositoryImpl(context)
    }

    @Provides
    fun providesAlbumsPresenter(presenter: AlbumsPresenterImpl): AlbumsPresenter {
        return presenter
    }

    @Provides
    fun providesAlbumDetailsPresenter(presenter: AlbumDetailsPresenterImpl): AlbumDetailsPresenter {
        return presenter
    }

    @Provides
    fun providesArtistDetailsPresenter(presenter: ArtistDetailsPresenterImpl): ArtistDetailsPresenter {
        return presenter
    }

    @Provides
    fun providesArtistsPresenter(presenter: ArtistsPresenterImpl): ArtistsPresenter {
        return presenter
    }

    @Provides
    fun providesGenresPresenter(presenter: GenresPresenterImpl): GenresPresenter {
        return presenter
    }

    @Provides
    fun providesGenreDetailsPresenter(presenter: GenreDetailsPresenterImpl): GenreDetailsPresenter {
        return presenter
    }

    @Provides
    fun providesHomePresenter(presenter: HomePresenterImpl): HomePresenter {
        return presenter
    }

    @Provides
    fun providesPlaylistSongPresenter(presenter: PlaylistSongsPresenterImpl): PlaylistSongsPresenter {
        return presenter
    }

    @Provides
    fun providesPlaylistsPresenter(presenter: PlaylistsPresenterImpl): PlaylistsPresenter {
        return presenter
    }

    @Provides
    fun providesSearchPresenter(presenter: SearchPresenterImpl): SearchPresenter {
        return presenter
    }

    @Provides
    fun providesSongPresenter(presenter: SongPresenterImpl): SongPresenter {
        return presenter
    }
}