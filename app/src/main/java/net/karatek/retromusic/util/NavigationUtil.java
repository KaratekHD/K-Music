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

package net.karatek.retromusic.util;

import static net.karatek.retromusic.Constants.RATE_ON_GOOGLE_PLAY;
import static net.karatek.retromusic.util.RetroUtil.openUrl;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import net.karatek.retromusic.R;
import net.karatek.retromusic.activities.AboutActivity;
import net.karatek.retromusic.activities.AlbumDetailsActivity;
import net.karatek.retromusic.activities.ArtistDetailActivity;
import net.karatek.retromusic.activities.GenreDetailsActivity;
import net.karatek.retromusic.activities.LicenseActivity;
import net.karatek.retromusic.activities.LyricsActivity;
import net.karatek.retromusic.activities.PlayingQueueActivity;
import net.karatek.retromusic.activities.PlaylistDetailActivity;
import net.karatek.retromusic.activities.PurchaseActivity;
import net.karatek.retromusic.activities.SearchActivity;
import net.karatek.retromusic.activities.SettingsActivity;
import net.karatek.retromusic.activities.SupportDevelopmentActivity;
import net.karatek.retromusic.activities.UserInfoActivity;
import net.karatek.retromusic.activities.WhatsNewActivity;
import net.karatek.retromusic.activities.bugreport.BugReportActivity;
import net.karatek.retromusic.helper.MusicPlayerRemote;
import net.karatek.retromusic.model.Genre;
import net.karatek.retromusic.model.Playlist;
import org.jetbrains.annotations.NotNull;


public class NavigationUtil {

    public static void bugReport(@NonNull Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, BugReportActivity.class), null);
    }

    public static void goToAbout(@NonNull Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, AboutActivity.class), null);
    }

    public static void goToAlbum(@NonNull Activity activity, int albumId) {
        Intent intent = new Intent(activity, AlbumDetailsActivity.class);
        intent.putExtra(AlbumDetailsActivity.EXTRA_ALBUM_ID, albumId);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToAlbumOptions(@NonNull Activity activity,
            int albumId,
            @NonNull ActivityOptions options) {
        Intent intent = new Intent(activity, AlbumDetailsActivity.class);
        intent.putExtra(AlbumDetailsActivity.EXTRA_ALBUM_ID, albumId);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void goToArtist(@NonNull Activity activity, int i) {
        Intent intent = new Intent(activity, ArtistDetailActivity.class);
        intent.putExtra(ArtistDetailActivity.EXTRA_ARTIST_ID, i);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToArtistOptions(@NotNull Activity activity,
            int artistId,
            @NonNull ActivityOptions options) {

        Intent intent = new Intent(activity, ArtistDetailActivity.class);
        intent.putExtra(ArtistDetailActivity.EXTRA_ARTIST_ID, artistId);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void goToGenre(@NonNull Activity activity, @NonNull Genre genre) {
        Intent intent = new Intent(activity, GenreDetailsActivity.class);
        intent.putExtra(GenreDetailsActivity.EXTRA_GENRE_ID, genre);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToLyrics(@NonNull Activity activity) {
        Intent intent = new Intent(activity, LyricsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToOpenSource(@NonNull Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, LicenseActivity.class), null);
    }

    public static void goToPlayStore(@NonNull Activity activity) {
        openUrl(activity, RATE_ON_GOOGLE_PLAY);
    }

    public static void goToPlayingQueue(@NonNull Activity activity) {
        Intent intent = new Intent(activity, PlayingQueueActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToPlaylistNew(@NonNull Activity activity, @NonNull Playlist playlist) {
        Intent intent = new Intent(activity, PlaylistDetailActivity.class);
        intent.putExtra(PlaylistDetailActivity.Companion.getEXTRA_PLAYLIST(), playlist);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToProVersion(@NonNull Context context) {
        ActivityCompat.startActivity(context, new Intent(context, PurchaseActivity.class), null);
    }

    public static void goToSearch(@NonNull Activity activity,
            @NonNull ActivityOptions activityOptions) {
        ActivityCompat.startActivity(activity, new Intent(activity, SearchActivity.class),
                activityOptions.toBundle());
    }

    public static void goToSearch(@NonNull Activity activity, boolean isMicOpen,
            @NonNull ActivityOptions activityOptions) {
        ActivityCompat.startActivity(activity, new Intent(activity, SearchActivity.class)
                        .putExtra(SearchActivity.EXTRA_SHOW_MIC, isMicOpen),
                activityOptions.toBundle());
    }

    public static void goToSettings(@NonNull Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, SettingsActivity.class), null);
    }

    public static void goToSupportDevelopment(@NonNull Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, SupportDevelopmentActivity.class), null);
    }

    public static void goToUserInfo(@NonNull Activity activity,
            @NonNull ActivityOptions activityOptions) {
        ActivityCompat.startActivity(activity, new Intent(activity, UserInfoActivity.class),
                activityOptions.toBundle());
    }

    public static void gotoWhatNews(@NonNull Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, WhatsNewActivity.class), null);
    }

    public static void openEqualizer(@NonNull final Activity activity) {
        stockEqalizer(activity);
    }

    private static void stockEqalizer(@NonNull Activity activity) {
        final int sessionId = MusicPlayerRemote.INSTANCE.getAudioSessionId();
        if (sessionId == AudioEffect.ERROR_BAD_VALUE) {
            Toast.makeText(activity, activity.getResources().getString(R.string.no_audio_ID),
                    Toast.LENGTH_LONG).show();
        } else {
            try {
                final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId);
                effects.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                activity.startActivityForResult(effects, 0);
            } catch (@NonNull final ActivityNotFoundException notFound) {
                Toast.makeText(activity, activity.getResources().getString(R.string.no_equalizer),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


}
