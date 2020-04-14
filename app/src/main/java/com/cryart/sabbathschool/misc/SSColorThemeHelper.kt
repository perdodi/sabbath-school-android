package com.cryart.sabbathschool.misc

import android.graphics.Color
import com.cryart.sabbathschool.model.SSReadingDisplayOptions

abstract class SSColorThemeHelper {
    companion object {

        /**
         * @return background color in integer for theme settings. Default theme is white.
         */
        fun getBackgroundColorFromSSReadingDisplayOptions( ssReadingDisplayOptions: SSReadingDisplayOptions?): Int {
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
    }
}