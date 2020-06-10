package net.karatek.retromusic.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import code.name.monkey.appthemehelper.util.ATHUtil
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import net.karatek.retromusic.Constants.APP_INSTAGRAM_LINK
import net.karatek.retromusic.Constants.APP_TELEGRAM_LINK
import net.karatek.retromusic.Constants.APP_TWITTER_LINK
import net.karatek.retromusic.Constants.FAQ_LINK
import net.karatek.retromusic.Constants.GITHUB_PROJECT
import net.karatek.retromusic.Constants.PINTEREST
import net.karatek.retromusic.Constants.RATE_ON_GOOGLE_PLAY
import net.karatek.retromusic.Constants.TELEGRAM_CHANGE_LOG
import net.karatek.retromusic.Constants.TRANSLATE
import net.karatek.retromusic.R
import net.karatek.retromusic.activities.base.AbsBaseActivity
import net.karatek.retromusic.adapter.ContributorAdapter
import net.karatek.retromusic.model.Contributor
import net.karatek.retromusic.util.NavigationUtil
import net.karatek.retromusic.util.PreferenceUtil
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_about.toolbar
import kotlinx.android.synthetic.main.card_credit.recyclerView
import kotlinx.android.synthetic.main.card_other.changelog
import kotlinx.android.synthetic.main.card_other.openSource
import kotlinx.android.synthetic.main.card_other.version
import kotlinx.android.synthetic.main.card_retro_info.appGithub
import kotlinx.android.synthetic.main.card_retro_info.appRate
import kotlinx.android.synthetic.main.card_retro_info.appShare
import kotlinx.android.synthetic.main.card_retro_info.appTranslation
import kotlinx.android.synthetic.main.card_retro_info.bugReportLink
import kotlinx.android.synthetic.main.card_retro_info.donateLink
import kotlinx.android.synthetic.main.card_retro_info.faqLink
import kotlinx.android.synthetic.main.card_social.instagramLink
import kotlinx.android.synthetic.main.card_social.pinterestLink
import kotlinx.android.synthetic.main.card_social.telegramLink
import kotlinx.android.synthetic.main.card_social.twitterLink
import java.io.IOException
import java.nio.charset.StandardCharsets

class AboutActivity : AbsBaseActivity(), View.OnClickListener {

    private val assetJsonData: String?
        get() {
            val json: String
            try {
                val inputStream = assets.open("contributors.json")
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                json = String(buffer, StandardCharsets.UTF_8)
            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
            }

            return json
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        setDrawUnderStatusBar()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setStatusbarColorAuto()
        setNavigationbarColorAuto()
        setLightNavigationBar(true)

        val toolbarColor = ATHUtil.resolveColor(this, R.attr.colorSurface)
        toolbar.setBackgroundColor(toolbarColor)
        ToolbarContentTintHelper.colorBackButton(toolbar)
        setSupportActionBar(toolbar)
        version.setSummary(getAppVersion())
        setUpView()
        loadContributors()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun setUpView() {
        appGithub.setOnClickListener(this)
        faqLink.setOnClickListener(this)
        telegramLink.setOnClickListener(this)
        appRate.setOnClickListener(this)
        appTranslation.setOnClickListener(this)
        appShare.setOnClickListener(this)
        donateLink.setOnClickListener(this)
        instagramLink.setOnClickListener(this)
        twitterLink.setOnClickListener(this)
        changelog.setOnClickListener(this)
        openSource.setOnClickListener(this)
        pinterestLink.setOnClickListener(this)
        bugReportLink.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.pinterestLink -> openUrl(PINTEREST)
            R.id.faqLink -> openUrl(FAQ_LINK)
            R.id.telegramLink -> openUrl(APP_TELEGRAM_LINK)
            R.id.appGithub -> openUrl(GITHUB_PROJECT)
            R.id.appTranslation -> openUrl(TRANSLATE)
            R.id.appRate -> openUrl(RATE_ON_GOOGLE_PLAY)
            R.id.appShare -> shareApp()
            R.id.donateLink -> NavigationUtil.goToSupportDevelopment(this)
            R.id.instagramLink -> openUrl(APP_INSTAGRAM_LINK)
            R.id.twitterLink -> openUrl(APP_TWITTER_LINK)
            R.id.changelog -> showChangeLogOptions()
            R.id.openSource -> NavigationUtil.goToOpenSource(this)
            R.id.bugReportLink -> NavigationUtil.bugReport(this)
        }
    }

    private fun showChangeLogOptions() {
        MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            cornerRadius(PreferenceUtil.getInstance(this@AboutActivity).dialogCorner)
            listItems(items = listOf("Telegram Channel", "App")) { _, position, _ ->
                if (position == 0) {
                    openUrl(TELEGRAM_CHANGE_LOG)
                } else {
                    NavigationUtil.gotoWhatNews(this@AboutActivity)
                }
            }
        }
    }

    private fun getAppVersion(): String {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "0.0.0"
        }
    }

    private fun shareApp() {
        ShareCompat.IntentBuilder.from(this).setType("text/plain")
            .setChooserTitle(R.string.share_app)
            .setText(String.format(getString(R.string.app_share), packageName)).startChooser()
    }

    private fun loadContributors() {
        val data = assetJsonData
        val type = object : TypeToken<List<Contributor>>() {

        }.type
        val contributors = Gson().fromJson<List<Contributor>>(data, type)

        val contributorAdapter = ContributorAdapter(contributors)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = contributorAdapter
    }
}
