package com.mynanodegreeapps.bakingapp.activity;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mynanodegreeapps.bakingapp.R;
import com.mynanodegreeapps.bakingapp.util.BakingActivityIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BakingActivityTest {

    private BakingActivityIdlingResource idlingResource;

    @Rule
    public ActivityTestRule<BakingActivity> mActivityTestRule = new ActivityTestRule<>(BakingActivity.class);

    // Register any resource that need to be synchronized with Espresso before the test is run
    @Before
    public void registerIdlingResource() throws NoSuchFieldException {
        BakingActivity activity = mActivityTestRule.getActivity();
        idlingResource = new BakingActivityIdlingResource(activity);

        Espresso.registerIdlingResources(idlingResource);
    }

    @Test
    public void bakingActivityTest() {

        // Click on Recipe Recycler View
        onView(withId(R.id.recipeGrid)).perform(actionOnItemAtPosition(1, click()));
        // Click on Steps Recycler View
        onView(withId(R.id.recipeSteps)).perform(actionOnItemAtPosition(1,click()));
        // Click on Ingredient's next
        onView(withId(R.id.btn_next)).perform(click());

        // Verify that ExoPlayer exits !
        onView(withId(R.id.videoContainer)).check(matches(isDisplayed()));

    }

    @After
    public void unregisterIdlingResource(){
        if(idlingResource != null){
            Espresso.unregisterIdlingResources(idlingResource);
        }
    }

}
