/*
 * Copyright (c) 2020 Adventech <info@adventech.io>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.cryart.sabbathschool.misc.jobs

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.cryart.sabbathschool.R
import com.cryart.sabbathschool.misc.SSColorTheme
import com.cryart.sabbathschool.misc.SSConstants
import com.cryart.sabbathschool.misc.SSHelper
import com.cryart.sabbathschool.ui.splash.SplashActivity
import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import java.util.Calendar

class SSReminderJob : Job() {

    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }

    override fun onRunJob(params: Params): Result {
        return if (isCanceled) {
            Result.FAILURE
        } else {
            showNotification(context.applicationContext)
            scheduleAlarm(context.applicationContext)
            Result.SUCCESS
        }
    }

    private fun showNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName: String = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        val contentIntent = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0,
                contentIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_stat_notification)
                .setContentTitle(context.getString(R.string.ss_app_name))
                .setColor(Color.parseColor(SSColorTheme.getInstance().colorPrimary))
                .addAction(0, context.getString(R.string.ss_menu_read_now), pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000))
                .setContentIntent(pendingIntent)
                .setContentText(context.getString(R.string.ss_settings_reminder_text))

        notificationManager.notify(1, builder.build())
    }

    companion object {
        const val TAG = "SSReminderJob:tag"
        private const val CHANNEL_ID = "ss_notification_channel"

        @JvmStatic
        fun scheduleAlarm(context: Context) {
            val ssPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            var jobId = ssPreferences.getInt(SSConstants.SS_REMINDER_JOB_ID, -1)
            JobManager.instance().cancel(jobId)

            val reminderEnabled = ssPreferences.getBoolean(SSConstants.SS_SETTINGS_REMINDER_ENABLED_KEY,
                    SSConstants.SS_SETTINGS_REMINDER_ENABLED_DEFAULT_VALUE)
            if (!reminderEnabled) return

            val reminderTime = Calendar.getInstance()
            val currentTimeInMillis = reminderTime.timeInMillis
            val settingsReminderTime = ssPreferences.getString(SSConstants.SS_SETTINGS_REMINDER_TIME_KEY,
                    SSConstants.SS_SETTINGS_REMINDER_TIME_DEFAULT_VALUE)

            reminderTime[Calendar.HOUR_OF_DAY] = SSHelper.parseHourFromString(settingsReminderTime,
                    SSConstants.SS_REMINDER_TIME_SETTINGS_FORMAT)
            reminderTime[Calendar.MINUTE] = SSHelper.parseMinuteFromString(settingsReminderTime,
                    SSConstants.SS_REMINDER_TIME_SETTINGS_FORMAT)
            reminderTime[Calendar.SECOND] = 0

            if (reminderTime.timeInMillis <= currentTimeInMillis) {
                reminderTime.add(Calendar.DAY_OF_YEAR, 1)
            }
            val offset = reminderTime.timeInMillis - currentTimeInMillis

            jobId = JobRequest.Builder(TAG)
                    .setExact(offset)
                    .build()
                    .schedule()

            ssPreferences.edit { putInt(SSConstants.SS_REMINDER_JOB_ID, jobId) }
        }
    }
}