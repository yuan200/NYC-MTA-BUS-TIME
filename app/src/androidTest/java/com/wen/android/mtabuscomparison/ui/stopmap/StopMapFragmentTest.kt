package com.wen.android.mtabuscomparison.ui.stopmap

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.ui.MainActivityTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class StopMapFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityRule = MainActivityTestRule(R.id.stopMapFragment)
    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun showMap() {
        onView(withId(R.id.stop_map_map_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showSearchButton() {
        onView(withId(R.id.stop_map_search_icon))
            .check(matches(isDisplayed()))
    }
}