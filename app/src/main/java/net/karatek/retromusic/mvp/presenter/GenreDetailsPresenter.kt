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

package net.karatek.retromusic.mvp.presenter

import net.karatek.retromusic.Result
import net.karatek.retromusic.model.Song
import net.karatek.retromusic.mvp.BaseView
import net.karatek.retromusic.mvp.Presenter
import net.karatek.retromusic.mvp.PresenterImpl
import net.karatek.retromusic.providers.interfaces.Repository
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


/**
 * Created by hemanths on 20/08/17.
 */

interface GenreDetailsView : BaseView {
    fun songs(songs: ArrayList<Song>)
}

interface GenreDetailsPresenter : Presenter<GenreDetailsView> {
    fun loadGenreSongs(genreId: Int)

    class GenreDetailsPresenterImpl @Inject constructor(
            private val repository: Repository
    ) : PresenterImpl<GenreDetailsView>(), GenreDetailsPresenter, CoroutineScope {
        private val job = Job()

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.IO + job

        override fun detachView() {
            super.detachView()
            job.cancel()
        }


        override fun loadGenreSongs(genreId: Int) {
            launch {
                when (val result = repository.getGenre(genreId)) {
                    is Result.Success -> withContext(Dispatchers.Main) {
                        view?.songs(result.data)
                    }
                    is Result.Error -> withContext(Dispatchers.Main) {
                        view?.showEmptyView()
                    }
                }
            }
        }
    }
}
