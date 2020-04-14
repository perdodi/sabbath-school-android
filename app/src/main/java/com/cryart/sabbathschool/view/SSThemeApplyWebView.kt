package com.cryart.sabbathschool.view

import android.content.Context
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.webkit.WebView
import com.cryart.sabbathschool.misc.SSColorThemeHelper.Companion.getBackgroundColorFromSSReadingDisplayOptions
import com.cryart.sabbathschool.misc.SSConstants
import com.cryart.sabbathschool.model.SSReadingDisplayOptions

open class SSThemeApplyWebView
@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    init{
        setBackgroundColorFromTheme(context)
    }

    private fun setBackgroundColorFromTheme(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val ssReadingDisplayOptions = SSReadingDisplayOptions(
                prefs.getString(SSConstants.SS_SETTINGS_THEME_KEY, SSReadingDisplayOptions.SS_THEME_LIGHT),
                prefs.getString(SSConstants.SS_SETTINGS_SIZE_KEY, SSReadingDisplayOptions.SS_SIZE_MEDIUM),
                prefs.getString(SSConstants.SS_SETTINGS_FONT_KEY, SSReadingDisplayOptions.SS_FONT_LATO)
        )
        setBackgroundColor(getBackgroundColorFromSSReadingDisplayOptions(ssReadingDisplayOptions))
    }
}