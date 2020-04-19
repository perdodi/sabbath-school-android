package com.cryart.sabbathschool.misc

import android.content.res.Configuration
import android.graphics.Color
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.cryart.sabbathschool.R
import com.cryart.sabbathschool.model.SSReadingDisplayOptions

abstract class SSColorThemeHelper {



    companion object {
        /**
         * @return background color in integer for theme settings. Default theme is white.
         */
        fun getBackgroundColorFromSSReadingDisplayOptions(ssReadingDisplayOptions: SSReadingDisplayOptions?): Int {
            return Color.parseColor(
                    ssReadingDisplayOptions?.let {
                        when (ssReadingDisplayOptions.theme) {
                            SSReadingDisplayOptions.SS_THEME_DARK -> SSReadingDisplayOptions.SS_THEME_DARK_RGB
                            SSReadingDisplayOptions.SS_THEME_SEPIA -> SSReadingDisplayOptions.SS_THEME_SEPIA_RGB
                            else -> SSReadingDisplayOptions.SS_THEME_LIGHT_RGB
                        }
                    } ?: run {
                        SSReadingDisplayOptions.SS_THEME_LIGHT_RGB
                    }
            )
        }

      /*

      fun setTheme(theme: String) {
            when (theme) {
                SSReadingDisplayOptions.SS_THEME_DARK -> {

                }
                SSReadingDisplayOptions.SS_THEME_SEPIA -> {
                    //activity.setTheme(R.style.AppTheme_NoActionBar_ThemeSepia)
                    //activity.recreate()
                }
                SSReadingDisplayOptions.SS_THEME_LIGHT -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

            }
        }


        fun updateConfiguration(activity: AppCompatActivity, conf: Configuration) {
            val themeFromSP: String = PreferenceManager.getDefaultSharedPreferences(activity.baseContext).getString(SSConstants.SS_SETTINGS_THEME_KEY, "not_set")!!

            if (themeFromSP != "not_set") {
                when (conf.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        // Night mode is not active, we're using the light theme

                    }

                    Configuration.UI_MODE_NIGHT_YES -> {
                        // Night mode is active, we're using dark theme

                    }
                }

            }
        }

        */
    }
}