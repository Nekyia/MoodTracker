package jullien.matthieu.moodtracker.Controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jullien.matthieu.moodtracker.Model.MoodInfo;
import jullien.matthieu.moodtracker.R;
import jullien.matthieu.moodtracker.View.MoodFragment;
import jullien.matthieu.moodtracker.View.VerticalViewPager;


public class MainActivity extends FragmentActivity implements View.OnClickListener {
    public static final String PREFERENCES_KEY = "data";
    private static SharedPreferences mPreferences;
    // The pager widget, which handles animation and allows swiping vertically
    private VerticalViewPager mPager;

    // The last mood chosen for this day. By default, set to happy.
    private int mCurrentMood = MoodInfo.HAPPY_INDEX;

    private ImageView mImageNote;
    private ImageView mImageHistory;
    private String mNote = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get current mood and note
        mPreferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        mCurrentMood = mPreferences.getInt("currentMood", MoodInfo.HAPPY_INDEX);
        mNote = mPreferences.getString("note", null);

        mPager = findViewById(R.id.pager);

        // The pager adapter, which provides the pages to the view pager widget.
        PagerAdapter mPagerAdapter = new MoodPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // Callback interface for responding to changing state of the selected page
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            // Save the mood corresponding to this page in mCurrentMood
            @Override
            public void onPageSelected(int position) {
                mCurrentMood = position;
                mPreferences.edit().putInt("currentMood", mCurrentMood).apply();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });


        HistoryDbHelper dbHelper = new HistoryDbHelper(this);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        Date lastTimeSaved = dbHelper.getLastDate();
        Date today = Calendar.getInstance().getTime();
        if (lastTimeSaved == null || !(fmt.format(today).equals(fmt.format(lastTimeSaved)))) {
            // Save the mood
            dbHelper.addNewDay(mCurrentMood, mNote);
            resetMood();
        }
        dbHelper.close();

        // Set the current page to the current mood
        mPager.setCurrentItem(mCurrentMood);

        mImageNote = findViewById(R.id.main_note_image);
        mImageHistory = findViewById(R.id.main_history_image);
        mImageNote.setOnClickListener(this);
        mImageHistory.setOnClickListener(this);
    }

    public void resetMood() {
        mCurrentMood = MoodInfo.HAPPY_INDEX;
        mNote = null;
        mPreferences.edit().putInt("currentMood", mCurrentMood).apply();
        mPreferences.edit().putString("note", mNote).apply();
        mPager.setCurrentItem(mCurrentMood);
        if (this.hasWindowFocus()) {
            Toast.makeText(MainActivity.this, "MoodTracker : Une nouvelle journée débute...", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mImageNote) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.comment);

            // Set up the input
            final EditText input = new EditText(this);
            input.setHint(mPreferences.getString("note", ""));
            input.setHintTextColor(ResourcesCompat.getColor(getResources(), R.color.warm_grey, null));

            // Specify the type of input expected
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Save the note
                    mNote = input.getText().toString();
                    mPreferences.edit().putString("note", mNote).apply();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else if (view == mImageHistory) {
            Intent historyActivity = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(historyActivity);
        }
    }

    // FragmentPageAdapter which provide the pages as fragments
    private class MoodPagerAdapter extends FragmentPagerAdapter {
        private MoodPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MoodFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return MoodInfo.NB_MOOD;
        }
    }
}
