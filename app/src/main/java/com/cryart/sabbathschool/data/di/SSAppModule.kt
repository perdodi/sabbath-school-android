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

package com.cryart.sabbathschool.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.cryart.sabbathschool.SSApplication
import com.cryart.sabbathschool.data.repository.QuarterliesRepository
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module(includes = [FirebaseModule::class])
class SSAppModule {

    @Provides
    @Singleton
    fun provideContext(app: SSApplication): Context = app

    @Provides
    @Singleton
    fun provideSharedPrefs(context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun provideRepository(firebaseDatabase: FirebaseDatabase, prefs: SharedPreferences): QuarterliesRepository =
            QuarterliesRepository(firebaseDatabase, prefs)

    @Provides
    @Singleton
    @Named("backgroundCoroutineContext")
    fun provideBackgroundCoroutineContext(): CoroutineContext = Dispatchers.IO
}