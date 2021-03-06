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

package net.karatek.retromusic.glide.artistimage

import android.content.Context
import net.karatek.retromusic.deezer.Data
import net.karatek.retromusic.deezer.DeezerApiService
import net.karatek.retromusic.util.MusicUtil
import net.karatek.retromusic.util.PreferenceUtil
import com.bumptech.glide.Priority
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GenericLoaderFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.stream.StreamModelLoader
import okhttp3.OkHttpClient
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


class ArtistImage(val artistName: String, val skipOkHttpCache: Boolean)

class ArtistImageFetcher(
        private val context: Context,
        private val deezerApiService: DeezerApiService,
        val model: ArtistImage,
        val urlLoader: ModelLoader<GlideUrl, InputStream>,
        val width: Int,
        val height: Int
) : DataFetcher<InputStream> {

    private var urlFetcher: DataFetcher<InputStream>? = null
    private var isCancelled: Boolean = false

    override fun cleanup() {
        urlFetcher?.cleanup()
    }

    override fun getId(): String {
        return model.artistName
    }

    override fun cancel() {
        isCancelled = true
        urlFetcher?.cancel()
    }

    override fun loadData(priority: Priority?): InputStream? {
        if (!MusicUtil.isArtistNameUnknown(model.artistName) && PreferenceUtil.isAllowedToDownloadMetadata(context)) {
            val artists = model.artistName.split(",")
            val response = deezerApiService.getArtistImage(artists[0]).execute()

            if (!response.isSuccessful) {
                throw   IOException("Request failed with code: " + response.code());
            }

            if (isCancelled) return null

            return try {
                val deezerResponse = response.body();
                val imageUrl = deezerResponse?.data?.get(0)?.let { getHighestQuality(it) }
                val glideUrl = GlideUrl(imageUrl)
                urlFetcher = urlLoader.getResourceFetcher(glideUrl, width, height)
                urlFetcher?.loadData(priority)
            } catch (e: Exception) {
                null
            }
        } else return null
    }

    private fun getHighestQuality(imageUrl: Data): String {
        return when {
            imageUrl.pictureXl.isNotEmpty() -> imageUrl.pictureXl
            imageUrl.pictureBig.isNotEmpty() -> imageUrl.pictureBig
            imageUrl.pictureMedium.isNotEmpty() -> imageUrl.pictureMedium
            imageUrl.pictureSmall.isNotEmpty() -> imageUrl.pictureSmall
            imageUrl.picture.isNotEmpty() -> imageUrl.picture
            else -> ""
        }
    }
}

class ArtistImageLoader(
        val context: Context,
        private val deezerApiService: DeezerApiService,
        private val urlLoader: ModelLoader<GlideUrl, InputStream>
) : StreamModelLoader<ArtistImage> {

    override fun getResourceFetcher(model: ArtistImage, width: Int, height: Int): DataFetcher<InputStream> {
        return ArtistImageFetcher(context, deezerApiService, model, urlLoader, width, height)
    }
}

class Factory(
        val context: Context
) : ModelLoaderFactory<ArtistImage, InputStream> {
    private var deezerApiService: DeezerApiService
    private var okHttpFactory: OkHttpUrlLoader.Factory

    init {
        okHttpFactory = OkHttpUrlLoader.Factory(OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .build())
        deezerApiService = DeezerApiService.invoke(DeezerApiService.createDefaultOkHttpClient(context)
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .build())
    }

    override fun build(context: Context?, factories: GenericLoaderFactory?): ModelLoader<ArtistImage, InputStream> {
        return ArtistImageLoader(context!!, deezerApiService, okHttpFactory.build(context, factories))
    }

    override fun teardown() {
        okHttpFactory.teardown()
    }

    companion object {
        // we need these very low values to make sure our artist image loading calls doesn't block the image loading queue
        private const val TIMEOUT: Long = 700
    }

}
