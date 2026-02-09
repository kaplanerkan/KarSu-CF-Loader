package com.karsu.cfl.sample

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun loaderWidgetIsDisplayed() {
        onView(withId(R.id.karSuCfLoaders))
            .check(matches(isDisplayed()))
    }

    @Test
    fun recyclerViewDemoButtonIsDisplayed() {
        onView(withId(R.id.btnRecyclerViewDemo))
            .check(matches(isDisplayed()))
    }

    @Test
    fun textControlsDemoButtonIsDisplayed() {
        onView(withId(R.id.btnTextControlsDemo))
            .check(matches(isDisplayed()))
    }

    @Test
    fun recyclerViewDemoButtonClickOpensActivity() {
        onView(withId(R.id.btnRecyclerViewDemo))
            .perform(click())
        onView(withText(R.string.recyclerview_title))
            .check(matches(isDisplayed()))
    }

    @Test
    fun textControlsDemoButtonClickOpensActivity() {
        onView(withId(R.id.btnTextControlsDemo))
            .perform(click())
        onView(withText(R.string.text_controls_title))
            .check(matches(isDisplayed()))
    }

    @Test
    fun progressSliderIsDisplayed() {
        onView(withId(R.id.seekBarProgress))
            .check(matches(isDisplayed()))
    }

    @Test
    fun controlsCardIsDisplayed() {
        onView(withId(R.id.controlsCard))
            .check(matches(isDisplayed()))
    }
}
