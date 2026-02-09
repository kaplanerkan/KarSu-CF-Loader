package com.karsu.cfl.sample

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecyclerViewActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RecyclerViewActivity::class.java)

    @Test
    fun recyclerViewIsDisplayed() {
        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun toolbarShowsTitle() {
        onView(withText(R.string.recyclerview_title))
            .check(matches(isDisplayed()))
    }

    @Test
    fun infoCardIsInitiallyHidden() {
        onView(withId(R.id.infoCard))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun firstItemIsDisplayed() {
        onView(withText("File Upload"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickingItemShowsInfoCard() {
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<LoaderAdapter.LoaderViewHolder>(0, click()))
        onView(withId(R.id.infoCard))
            .check(matches(isDisplayed()))
        onView(withId(R.id.textItemInfo))
            .check(matches(isDisplayed()))
    }
}
