package com.mynanodegreeapps.bakingapp.activity;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.mynanodegreeapps.bakingapp.R;
import com.mynanodegreeapps.bakingapp.util.BakingActivityIdlingResource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BakingActivityWorkflowTest {

    private BakingActivityIdlingResource idlingResource;

    @Rule
    public ActivityTestRule<BakingActivity> mActivityTestRule = new ActivityTestRule<>(BakingActivity.class);

    // Register any resource that need to be synchronized with Espresso before the test is run
    @Before
    public void registerIdlingResource(){
        BakingActivity activity = mActivityTestRule.getActivity();
        idlingResource = new BakingActivityIdlingResource(activity);

        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void unregisterIdlingResource(){
        if(idlingResource != null){
            Espresso.unregisterIdlingResources(idlingResource);
        }
    }
}
