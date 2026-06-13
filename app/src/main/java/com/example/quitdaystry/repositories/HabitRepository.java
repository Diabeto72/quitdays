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
import com.example.quitdaystry.models.HabitHistory;
import com.example.quitdaystry.utils.DateUtils;

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

    /** Async wrapper — call from UI code (e.g. MainActivity.onResume). */
    public void autoMarkCleanDays() {
        executor.execute(this::autoMarkCleanDaysSync);
    }

    /**
     * Automatically logs every day from the habit's quit date up to today as CLEAN,
     * unless a log already exists for that date (so BREAK days are never overwritten).
     * Returns the number of active habits processed.
     */
    public int autoMarkCleanDaysSync() {
        List<Long> ids = dao.getActiveHabitIdsSync();
        if (ids == null || ids.isEmpty()) return 0;

        LocalDate today = LocalDate.now();
        for (long id : ids) {
            Habit h = dao.getHabitByIdSync(id);
            if (h == null || h.getQuitDate() == null) continue;

            // Quit date itself is day 0 — savings start accruing from day 1.
            // Start from the day after the newest existing log (no gaps possible below it).
            LocalDate start = h.getQuitDate().plusDays(1);
            for (DayLog l : dao.getLogsForHabitSync(id)) {
                if (l.getLogDate() != null && l.getLogDate().plusDays(1).isAfter(start)) {
                    start = l.getLogDate().plusDays(1);
                }
            }

            for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
                DayLog log = new DayLog();
                log.setHabitId(id);
                log.setLogDate(d);
                log.setStatus(LogStatus.CLEAN);
                dao.insertLogIfAbsent(log);
            }
        }
        return ids.size();
    }

    public LiveData<List<HabitHistory>> getAllHabitHistory() {
        return dao.getAllHabitHistory();
    }

    /**
     * Ends a habit attempt: snapshots its final stats into history (so the stats
     * screen keeps showing it), then deletes the habit and its logs.
     */
    public void finalizeHabit(long habitId, LocalDate endDate, String failureNote) {
        executor.execute(() -> {
            Habit habit = dao.getHabitByIdSync(habitId);
            if (habit == null) return;

            List<DayLog> logs = dao.getLogsForHabitSync(habitId);
            int streakNow = DateUtils.currentStreak(habit, logs);
            int bestStreak = Math.max(streakNow, habit.getBestStreak());

            HabitHistory history = new HabitHistory();
            history.setName(habit.getName());
            history.setCategory(habit.getCategory());
            history.setColorHex(habit.getColorHex());
            history.setCurrency(habit.getCurrency());
            history.setDailyCost(habit.getDailyCost());
            history.setStartDate(habit.getQuitDate());
            history.setEndDate(endDate);
            history.setCleanDays(dao.getCleanCountSync(habitId));
            history.setBestStreak(bestStreak);
            history.setFailureNote(failureNote);
            dao.insertHabitHistory(history);

            dao.deleteHabit(habit);
        });
    }
}
