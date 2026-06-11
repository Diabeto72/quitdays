package com.example.quitdaystry.repositories;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.quitdaystry.db.AppDatabase;
import com.example.quitdaystry.db.dao.AppDao;
import com.example.quitdaystry.models.DayLog;
import com.example.quitdaystry.models.DayLog.LogStatus;
import com.example.quitdaystry.models.Habit;
import com.example.quitdaystry.models.Habit.HabitWithLogs;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/** Single source of truth for habit and log data. All writes run on a background thread. */
public class HabitRepository {

    private static volatile HabitRepository instance;

    private final AppDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private HabitRepository(Application app) {
        dao = AppDatabase.getInstance(app).appDao();
    }

    public static HabitRepository getInstance(Application app) {
        if (instance == null) {
            synchronized (HabitRepository.class) {
                if (instance == null) instance = new HabitRepository(app);
            }
        }
        return instance;
    }

    public LiveData<List<HabitWithLogs>> getActiveHabits() {
        return dao.getActiveHabitsWithLogs();
    }

    public LiveData<HabitWithLogs> getHabitWithLogs(long id) {
        return dao.getHabitWithLogs(id);
    }

    public LiveData<Habit> getHabit(long id) {
        return dao.getHabitById(id);
    }

    public LiveData<Integer> getCleanDaysCount(long habitId) {
        return dao.getCleanCount(habitId);
    }

    public void insertHabit(Habit h, Consumer<Long> onResult) {
        executor.execute(() -> {
            long id = dao.insertHabit(h);
            if (onResult != null) mainHandler.post(() -> onResult.accept(id));
        });
    }

    public void updateHabit(Habit h) {
        executor.execute(() -> dao.updateHabit(h));
    }

    public void deleteHabit(Habit h) {
        executor.execute(() -> dao.deleteHabit(h));
    }

    public void logCleanDay(long habitId, LocalDate date, Integer craving, String notes) {
        executor.execute(() -> {
            DayLog log = new DayLog();
            log.setHabitId(habitId);
            log.setLogDate(date);
            log.setStatus(LogStatus.CLEAN);
            log.setCravingLevel(craving);
            log.setNotes(notes);
            dao.insertLog(log);
        });
    }

    public void logBreak(long habitId, LocalDate date, Integer craving, String trigger, String notes) {
        executor.execute(() -> {
            DayLog log = new DayLog();
            log.setHabitId(habitId);
            log.setLogDate(date);
            log.setStatus(LogStatus.BREAK);
            log.setCravingLevel(craving);
            log.setTriggerNote(trigger);
            log.setNotes(notes);
            dao.insertLog(log);

            Habit habit = dao.getHabitByIdSync(habitId);
            if (habit != null) {
                // Capture current streak as best_streak before resetting quitDate
                java.util.List<DayLog> existingLogs = dao.getLogsForHabitSync(habitId);
                int streakNow = com.example.quitdaystry.utils.DateUtils.currentStreak(habit, existingLogs);
                if (streakNow > habit.getBestStreak()) {
                    habit.setBestStreak(streakNow);
                }
                habit.setQuitDate(date.plusDays(1));
                dao.updateHabit(habit);
            }
        });
    }
}
