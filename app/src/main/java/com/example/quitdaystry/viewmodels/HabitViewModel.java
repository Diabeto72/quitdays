package com.example.quitdaystry.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.quitdaystry.models.DayLog;
import com.example.quitdaystry.models.Habit;
import com.example.quitdaystry.models.Habit.HabitWithLogs;
import com.example.quitdaystry.models.HabitHistory;
import com.example.quitdaystry.models.StatsEntry;
import com.example.quitdaystry.repositories.HabitRepository;
import com.example.quitdaystry.utils.DateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The single ViewModel of the app (MVVM).
 * Screens observe LiveData from here; all writes go through the Repository.
 */
public class HabitViewModel extends AndroidViewModel {

    private final HabitRepository repo;

    // For the detail/edit screens: which habit is currently open.
    private final MutableLiveData<Long> habitIdLive = new MutableLiveData<>();
    private final LiveData<HabitWithLogs> habitWithLogs;
    private final LiveData<Integer> cleanCount;
    private final MediatorLiveData<List<StatsEntry>> statsEntries = new MediatorLiveData<>();

    public HabitViewModel(@NonNull Application application) {
        super(application);
        repo = HabitRepository.getInstance(application);

        habitWithLogs = Transformations.switchMap(habitIdLive, id -> repo.getHabitWithLogs(id));
        cleanCount    = Transformations.switchMap(habitIdLive, id -> repo.getCleanDaysCount(id));

        LiveData<List<HabitWithLogs>> activeHabits = repo.getActiveHabits();
        LiveData<List<HabitHistory>> habitHistory = repo.getAllHabitHistory();
        statsEntries.addSource(activeHabits, active ->
                combineStatsEntries(active, habitHistory.getValue()));
        statsEntries.addSource(habitHistory, history ->
                combineStatsEntries(activeHabits.getValue(), history));
    }

    private void combineStatsEntries(List<HabitWithLogs> active, List<HabitHistory> history) {
        List<StatsEntry> entries = new ArrayList<>();
        if (active != null) {
            for (HabitWithLogs hwl : active) {
                List<DayLog> logs = hwl.logs != null ? hwl.logs : Collections.emptyList();
                long cleanDays = logs.stream().filter(l -> l.getStatus() == DayLog.LogStatus.CLEAN).count();
                entries.add(new StatsEntry(
                        hwl.habit.getName(),
                        hwl.habit.getCategory(),
                        hwl.habit.getColorHex(),
                        hwl.habit.getCurrency(),
                        hwl.habit.getDailyCost(),
                        (int) cleanDays,
                        DateUtils.currentStreak(hwl.habit, logs),
                        hwl.habit.getQuitDate(),
                        null,
                        true));
            }
        }
        if (history != null) {
            for (HabitHistory h : history) {
                entries.add(new StatsEntry(
                        h.getName(),
                        h.getCategory(),
                        h.getColorHex(),
                        h.getCurrency(),
                        h.getDailyCost(),
                        h.getCleanDays(),
                        h.getBestStreak(),
                        h.getStartDate(),
                        h.getEndDate(),
                        false));
            }
        }
        statsEntries.setValue(entries);
    }

    // --- List screen (HabitsFragment, StatsFragment) ---

    public LiveData<List<HabitWithLogs>> getActiveHabits() {
        return repo.getActiveHabits();
    }

    /** Combined list of active habits and ended attempts, for the stats screen. */
    public LiveData<List<StatsEntry>> getStatsEntries() {
        return statsEntries;
    }

    // --- Detail / edit screens ---

    public void setHabitId(long id) { habitIdLive.setValue(id); }

    public Long getCurrentHabitId() { return habitIdLive.getValue(); }

    public LiveData<HabitWithLogs> getHabitWithLogs() { return habitWithLogs; }

    public LiveData<Integer> getCleanCount() { return cleanCount; }

    /** Ends the current habit attempt: snapshots its stats to history and deletes it. */
    public void finalizeHabit(LocalDate date, String failureNote) {
        Long id = habitIdLive.getValue();
        if (id != null) repo.finalizeHabit(id, date, failureNote);
    }

    public void insert(Habit h) { repo.insertHabit(h, null); }

    public void update(Habit h) { repo.updateHabit(h); }

    public void delete() {
        HabitWithLogs hwl = habitWithLogs.getValue();
        if (hwl != null) repo.deleteHabit(hwl.habit);
    }
}
