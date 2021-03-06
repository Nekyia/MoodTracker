package jullien.matthieu.moodtracker;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import jullien.matthieu.moodtracker.Controller.HistoryActivity;
import jullien.matthieu.moodtracker.Controller.MainActivity;
import jullien.matthieu.moodtracker.Model.MoodInfo;
import jullien.matthieu.moodtracker.View.VerticalViewPager;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test the main activity
 */
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private MainActivity mActivity;
    private SharedPreferences mSharedPreferences;
    private VerticalViewPager mPager;

    /**
     * Prepare the main activity
     */
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        mSharedPreferences = mActivity.getApplicationContext().getSharedPreferences(MainActivity.PREFERENCES_KEY, Context.MODE_PRIVATE);
        mPager = mActivity.findViewById(R.id.pager);
    }

    /**
     * Test if there is a correct number of pages in the pager's adapter
     */
    @Test
    public void test_mood_views() throws Exception {
        assertTrue(mPager.getAdapter().getCount() == MoodInfo.NB_MOOD);
    }

    /**
     * Test if swipe up change the mood
     */
    @Test
    public void test_swipe() throws Exception {
        final int SUPER_HAPPY_INDEX = 4;
        final boolean isLastIndex;

        int initialMood = mSharedPreferences.getInt("currentMood", -1);
        isLastIndex = (initialMood == SUPER_HAPPY_INDEX);

        int pageIndexBefore = mPager.getCurrentItem();
        onView(withId(R.id.pager)).perform(swipeUp());
        int pageIndexAfter = mPager.getCurrentItem();
        int finalMood = mSharedPreferences.getInt("currentMood", -1);

        if (isLastIndex) {
            assertEquals(initialMood, SUPER_HAPPY_INDEX);
            assertEquals(pageIndexBefore, pageIndexAfter);
        } else {
            assertEquals(initialMood, finalMood + 1);
            assertEquals(pageIndexBefore, pageIndexAfter + 1);
        }
    }

    /**
     * Test if the history button starts HistoryActivity
     */
    @Test
    public void test_launch_history() throws Exception {

        // register next activity that need to be monitored
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(HistoryActivity.class.getName(), null, false);

        // click on the history button
        onView(withId(R.id.main_history_image)).perform(click());

        //Wait for the activity with timeout
        Activity nextActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);

        // if nextActivity isn't null, a HistoryActivity has opened
        assertNotNull(nextActivity);
        nextActivity.finish();
    }

    /**
     * Close the activity
     */
    @After
    public void tearDown() throws Exception {
        mActivity.finish();
    }
}
