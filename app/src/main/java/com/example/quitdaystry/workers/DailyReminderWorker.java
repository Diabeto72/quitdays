package com.example.quitdaystry.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.quitdaystry.db.AppDatabase;
import com.example.quitdaystry.db.dao.AppDao;
import com.example.quitdaystry.utils.NotificationUtil;

import java.time.LocalDate;
import java.util.List;

public class DailyReminderWorker extends Worker {

    public DailyReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        AppDao dao = AppDatabase.getInstance(getApplicationContext()).appDao();

        List<Long> activeIds = dao.getActiveHabitIdsSync();
        if (activeIds == null || activeIds.isEmpty()) return Result.success();

        String today = LocalDate.now().toString();
        for (long id : activeIds) {
            if (dao.getLogForDate(id, today) == null) {
                NotificationUtil.showReminder(getApplicationContext(), "זכור לסמן את יום הגמילה שלך!");
                break;
            }
        }

        return Result.success();
    }
}
