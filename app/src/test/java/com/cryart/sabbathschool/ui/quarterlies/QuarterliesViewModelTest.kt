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

package com.cryart.sabbathschool.ui.quarterlies

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cryart.sabbathschool.data.model.Status
import com.cryart.sabbathschool.data.model.response.Resource
import com.cryart.sabbathschool.data.repository.QuarterliesRepository
import com.cryart.sabbathschool.model.SSQuarterly
import com.cryart.sabbathschool.observeFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@RunWith(AndroidJUnit4::class)
class QuarterliesViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mockRepository: QuarterliesRepository

    @Mock
    private lateinit var mockPreferences: SharedPreferences

    @Mock
    private lateinit var mockPreferencesEditor: SharedPreferences.Editor

    private lateinit var viewModel: QuarterliesViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @ExperimentalCoroutinesApi
    @Before
    fun setup() = runBlockingTest {
        Dispatchers.setMain(mainThreadSurrogate)

        val code = "en"
        `when`(mockPreferences.edit()).thenReturn(mockPreferencesEditor)
        `when`(mockPreferences.getString("ss_last_language_index", code)).thenReturn(code)
        val flow: Flow<Resource<List<SSQuarterly>>> = callbackFlow {
            this.sendBlocking(Resource.success(emptyList()))
        }
        `when`(mockRepository.getQuarterlies(code)).thenReturn(flow)

        viewModel = QuarterliesViewModel(mockRepository, mockPreferences, TestCoroutineDispatcher())
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should post last quarterly index if it exists on init`() {
        // given
        val quarterlyIndex = "en-2020-02-13"
        `when`(mockPreferences.getString("ss_last_quarterly_index", null)).thenReturn(quarterlyIndex)

        // when
        viewModel.viewCreated("en")

        // then
        viewModel.lastQuarterlyIndexLiveData.value shouldBeEqualTo quarterlyIndex
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should not post any quarterly index if it doesn't exists on init`() {
        // given
        `when`(mockPreferences.getString("ss_last_quarterly_index", null)).thenReturn(null)

        // when
        viewModel.viewCreated("en")

        // then
        viewModel.lastQuarterlyIndexLiveData.value shouldBeEqualTo null
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should update selected language and quarterlies list`() = runBlockingTest {
        // given
        val states = viewModel.viewStatusLiveData.observeFuture()
        val language = "de"
        val flow: Flow<Resource<List<SSQuarterly>>> = callbackFlow {
            this.sendBlocking(Resource.success(emptyList()))
            awaitClose {  }
        }
       `when`(mockRepository.getQuarterlies(language)).thenReturn(flow)

        // when
        viewModel.languageSelected(language)

        // then
        verify(mockPreferencesEditor).putString("ss_last_language_index", language)
        states shouldBeEqualTo listOf(
                Status.LOADING,
                Status.SUCCESS
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should set view status to error when get quarterlies returns error resource`() = runBlockingTest {
        // given
        val states = viewModel.viewStatusLiveData.observeFuture()
        val list = viewModel.quarterliesLiveData.observeFuture()
        val language = "de"
        val errorFlow: Flow<Resource<List<SSQuarterly>>> = callbackFlow {
            this.sendBlocking(Resource.error(Throwable()))
            awaitClose {  }
        }
        `when`(mockRepository.getQuarterlies(language)).thenReturn(errorFlow)

        // when
        viewModel.languageSelected(language)

        // then
        states shouldBeEqualTo listOf(
                Status.LOADING,
                Status.ERROR
        )
        list shouldBeEqualTo emptyList()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should filter out quarterlies with same group showing only the first one`() = runBlockingTest {
        // given
        val all = listOf(
                getQuarterly("one"),
                getQuarterly("three"),
                getQuarterly("two"),
                getQuarterly("one")
        )
        val language = "de"

        val flow: Flow<Resource<List<SSQuarterly>>> = callbackFlow {
            this.sendBlocking(Resource.success(all))
        }
        `when`(mockRepository.getQuarterlies(language)).thenReturn(flow)

        // when
        viewModel.languageSelected(language)

        // then
        viewModel.quarterliesLiveData.value shouldBeEqualTo all.dropLast(1)
    }

    private fun getQuarterly(group: String): SSQuarterly {
        return SSQuarterly().also { it.group = group }
    }
}