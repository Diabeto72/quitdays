package com.example.quitdaystry.workers;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.quitdaystry.repositories.HabitRepository;
import com.example.quitdaystry.utils.NotificationUtil;

/**
 * Runs once a day (WorkManager): auto-logs clean days for all active habits,
 * then sends an encouraging notification. Days are counted clean automatically —
 * the user only reports breaks.
 */
public class DailyReminderWorker extends Worker {

    public DailyReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        HabitRepository repo = HabitRepository.getInstance(
                (Application) getApplicationContext());

        int activeHabits = repo.autoMarkCleanDaysSync();
        if (activeHabits > 0) {
            NotificationUtil.showReminder(getApplicationContext(),
                    "יום נקי נוסף לרצף שלך! 💪 המשך כך");
        }

        return Result.success();
    }
}
