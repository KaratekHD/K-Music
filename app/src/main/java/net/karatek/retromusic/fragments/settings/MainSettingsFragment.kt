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

package net.karatek.retromusic.fragments.settings

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import code.name.monkey.appthemehelper.ThemeStore
import net.karatek.retromusic.App
import net.karatek.retromusic.R
import net.karatek.retromusic.activities.SettingsActivity
import net.karatek.retromusic.extensions.hide
import net.karatek.retromusic.extensions.show
import net.karatek.retromusic.util.NavigationUtil
import kotlinx.android.synthetic.main.fragment_main_settings.aboutSettings
import kotlinx.android.synthetic.main.fragment_main_settings.audioSettings
import kotlinx.android.synthetic.main.fragment_main_settings.buyPremium
import kotlinx.android.synthetic.main.fragment_main_settings.buyProContainer
import kotlinx.android.synthetic.main.fragment_main_settings.diamondIcon
import kotlinx.android.synthetic.main.fragment_main_settings.generalSettings
import kotlinx.android.synthetic.main.fragment_main_settings.imageSettings
import kotlinx.android.synthetic.main.fragment_main_settings.notificationSettings
import kotlinx.android.synthetic.main.fragment_main_settings.nowPlayingSettings
import kotlinx.android.synthetic.main.fragment_main_settings.otherSettings
import kotlinx.android.synthetic.main.fragment_main_settings.personalizeSettings

class MainSettingsFragment : Fragment(), View.OnClickListener {
    override fun onClick(view: View) {
        when (view.id) {
            R.id.generalSettings -> inflateFragment(ThemeSettingsFragment(), R.string.general_settings_title)
            R.id.audioSettings -> inflateFragment(AudioSettings(), R.string.pref_header_audio)
            R.id.nowPlayingSettings -> inflateFragment(NowPlayingSettingsFragment(), R.string.now_playing)
            R.id.personalizeSettings -> inflateFragment(PersonalizeSettingsFragment(), R.string.personalize)
            R.id.imageSettings -> inflateFragment(ImageSettingFragment(), R.string.pref_header_images)
            R.id.notificationSettings -> inflateFragment(NotificationSettingsFragment(), R.string.notification)
            R.id.otherSettings -> inflateFragment(OtherSettingsFragment(), R.string.others)
            R.id.aboutSettings -> NavigationUtil.goToAbout(requireActivity())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generalSettings.setOnClickListener(this)
        audioSettings.setOnClickListener(this)
        nowPlayingSettings.setOnClickListener(this)
        personalizeSettings.setOnClickListener(this)
        imageSettings.setOnClickListener(this)
        notificationSettings.setOnClickListener(this)
        otherSettings.setOnClickListener(this)
        aboutSettings.setOnClickListener(this)

        buyProContainer.apply {
            if (!App.isProVersion()) show() else hide()
            setOnClickListener {
                NavigationUtil.goToProVersion(requireContext())
            }
        }
        buyPremium.setOnClickListener {
            NavigationUtil.goToProVersion(requireContext())
        }
        ThemeStore.accentColor(requireContext()).let {
            buyPremium.setTextColor(it)
            diamondIcon.imageTintList = ColorStateList.valueOf(it)
        }
    }

    private fun inflateFragment(fragment: Fragment, @StringRes title: Int) {
        (requireActivity() as SettingsActivity).setupFragment(fragment, title)
    }
}