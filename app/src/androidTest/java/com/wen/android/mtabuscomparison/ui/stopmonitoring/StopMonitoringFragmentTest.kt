package com.wen.android.mtabuscomparison.ui.stopmonitoring;

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wen.android.mtabuscomparison.R
import com.wen.android.mtabuscomparison.ui.MainActivityTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
public class StopMonitoringFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityRule = MainActivityTestRule(
        R.id.stopMonitoringFragment,
        Bundle().apply {
            putString("stopId", "400555")
            putBoolean("isFavorite", false)
            putInt("dbRowId", -1)
        })

    @Test
    fun showStopMonitoringRecyclerView() {

        onView(withId(R.id.stop_monitoring_line_img))
            .check(matches(isDisplayed()))

        onView(withId(R.id.published_line))
            .check(matches(isDisplayed()))

        onView(withId(R.id.destination_name))
            .check(matches(isDisplayed()))

        onView(withId(R.id.expected_arrive_time))
            .check(matches(isDisplayed()))

        onView(withId(R.id.presentable_distance))
            .check(matches(isDisplayed()))

        onView(withId(R.id.next_bus_time))
            .check(matches(isDisplayed()))

        onView(withId(R.id.live_minute))
            .check(matches(isDisplayed()))

        onView(withId(R.id.stop_monitoring_signal))
            .check(matches(isDisplayed()))

        onView(withId(R.id.stop_monitoring_min))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showPublishedLineRecyclerView() {
        onView(withId(R.id.stop_monitoring_bus_code))
            .check(matches(isDisplayed()))
    }
}