package com.karsu.cfl.sample

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TextControlsActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(TextControlsActivity::class.java)

    @Test
    fun loaderWidgetIsDisplayed() {
        onView(withId(R.id.karSuCfLoadersNoSrc))
            .check(matches(isDisplayed()))
    }

    @Test
    fun toolbarShowsTitle() {
        onView(withText(R.string.text_controls_title))
            .check(matches(isDisplayed()))
    }

    @Test
    fun textInputIsDisplayed() {
        onView(withId(R.id.editTextOverlay))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showProgressSwitchIsDisplayed() {
        onView(withId(R.id.switchShowProgress))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showProgressSwitchIsCheckedByDefault() {
        onView(withId(R.id.switchShowProgress))
            .check(matches(isChecked()))
    }

    @Test
    fun showProgressSwitchCanBeToggled() {
        onView(withId(R.id.switchShowProgress))
            .perform(click())
            .check(matches(isNotChecked()))
    }

    @Test
    fun canTypeOverlayText() {
        onView(withId(R.id.editTextOverlay))
            .perform(typeText("KarSu"))
            .check(matches(withText("KarSu")))
    }

    @Test
    fun textSizeSliderIsDisplayed() {
        onView(withId(R.id.seekBarTextSize))
            .check(matches(isDisplayed()))
    }

    @Test
    fun textColorSliderIsDisplayed() {
        onView(withId(R.id.sliderTextColor))
            .check(matches(isDisplayed()))
    }

    @Test
    fun waveColorSliderIsDisplayed() {
        onView(withId(R.id.sliderWaveColor))
            .check(matches(isDisplayed()))
    }
}
