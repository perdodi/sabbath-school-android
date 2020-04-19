package com.cryart.sabbathschool.view

import android.content.Context
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.webkit.WebView
import com.cryart.sabbathschool.misc.SSColorThemeHelper.Companion.getBackgroundColorFromSSReadingDisplayOptions
import com.cryart.sabbathschool.model.SSReadingDisplayOptions

open class SSThemeApplyWebView
@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    init{
        setBackgroundColorFromTheme(context)
    }

    private fun setBackgroundColorFromTheme(context: Context) {
        setBackgroundColor(
                getBackgroundColorFromSSReadingDisplayOptions(
                        SSReadingDisplayOptions(
                            PreferenceManager.getDefaultSharedPreferences(context),
                            context.resources.configuration
        )))
    }
}