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
import net.karatek.retromusic.mvp.*
import net.karatek.retromusic.providers.interfaces.Repository
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Created by hemanths on 10/08/17.
 */
interface SongView {
	fun songs(songs: ArrayList<Song>)

	fun showEmptyView()
}

interface SongPresenter : Presenter<SongView> {
	fun loadSongs()
	class SongPresenterImpl @Inject constructor(
			private val repository: Repository
	) : PresenterImpl<SongView>(), SongPresenter, CoroutineScope {

		private var job: Job = Job()

		override val coroutineContext: CoroutineContext
			get() = Dispatchers.IO + job

		override fun loadSongs() {
			launch {
				when (val songs = repository.allSongs()) {
					is Result.Success -> withContext(Dispatchers.Main) { view?.songs(songs.data) }
					is Result.Error   -> withContext(Dispatchers.Main) { view?.showEmptyView() }
				}
			}
		}

		override fun detachView() {
			super.detachView()
			job.cancel();
		}
	}
}


