package com.example.quitdaystry.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.quitdaystry.models.Habit;
import com.example.quitdaystry.models.Habit.HabitWithLogs;
import com.example.quitdaystry.repositories.HabitRepository;

import java.time.LocalDate;
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

    public HabitViewModel(@NonNull Application application) {
        super(application);
        repo = HabitRepository.getInstance(application);

        habitWithLogs = Transformations.switchMap(habitIdLive, id -> repo.getHabitWithLogs(id));
        cleanCount    = Transformations.switchMap(habitIdLive, id -> repo.getCleanDaysCount(id));
    }

    // --- List screen (HabitsFragment, StatsFragment) ---

    public LiveData<List<HabitWithLogs>> getActiveHabits() {
        return repo.getActiveHabits();
    }

    // --- Detail / edit screens ---

    public void setHabitId(long id) { habitIdLive.setValue(id); }

    public Long getCurrentHabitId() { return habitIdLive.getValue(); }

    public LiveData<HabitWithLogs> getHabitWithLogs() { return habitWithLogs; }

    public LiveData<Integer> getCleanCount() { return cleanCount; }

    public void markClean(LocalDate date, Integer craving, String notes) {
        Long id = habitIdLive.getValue();
        if (id != null) repo.logCleanDay(id, date, craving, notes);
    }

    public void markBreak(LocalDate date, Integer craving, String trigger, String notes) {
        Long id = habitIdLive.getValue();
        if (id != null) repo.logBreak(id, date, craving, trigger, notes);
    }

    public void insert(Habit h) { repo.insertHabit(h, null); }

    public void update(Habit h) { repo.updateHabit(h); }

    public void delete() {
        HabitWithLogs hwl = habitWithLogs.getValue();
        if (hwl != null) repo.deleteHabit(hwl.habit);
    }
}
