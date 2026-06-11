package com.example.quitdaystry.activities;

import android.os.Bundle;
import androidx.preference.PreferenceManager;

import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.quitdaystry.R;
import com.example.quitdaystry.fragments.HabitsFragment;
import com.example.quitdaystry.fragments.SettingsFragment;
import com.example.quitdaystry.fragments.StatsFragment;
import com.example.quitdaystry.repositories.HabitRepository;
import com.example.quitdaystry.utils.NotificationUtil;
import com.example.quitdaystry.workers.DailyReminderWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity {

    public static final String WORK_DAILY_REMINDER = "daily_reminder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationUtil.createChannel(this);
        rescheduleReminderIfEnabled();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new HabitsFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.nav_habits) {
                fragment = new HabitsFragment();
            } else if (id == R.id.nav_stats) {
                fragment = new StatsFragment();
            } else if (id == R.id.nav_settings) {
                fragment = new SettingsFragment();
            } else {
                return false;
            }
            loadFragment(fragment);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Every day counts as clean automatically — backfill since last open
        HabitRepository.getInstance(getApplication()).autoMarkCleanDays();
    }

    /** Schedules or cancels the daily reminder based on stored preference. */
    public static void rescheduleReminderIfEnabled(android.content.Context context) {
        boolean enabled = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("reminder_enabled", false);
        WorkManager wm = WorkManager.getInstance(context);
        if (!enabled) {
            wm.cancelUniqueWork(WORK_DAILY_REMINDER);
            return;
        }
        int hour = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt("reminder_hour", 21);

        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);
        if (target.getTimeInMillis() <= System.currentTimeMillis()) {
            target.add(Calendar.DAY_OF_YEAR, 1);
        }
        long delayMs = target.getTimeInMillis() - System.currentTimeMillis();

        PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(
                DailyReminderWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .build();

        // Re-enqueue so a changed reminder hour takes effect (KEEP would ignore it)
        wm.enqueueUniquePeriodicWork(
                WORK_DAILY_REMINDER,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                work);
    }

    private void rescheduleReminderIfEnabled() {
        rescheduleReminderIfEnabled(this);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
