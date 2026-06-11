package com.example.quitdaystry.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.quitdaystry.models.DayLog;
import com.example.quitdaystry.models.Habit;
import com.example.quitdaystry.models.Habit.HabitWithLogs;
import com.example.quitdaystry.repositories.HabitRepository;

import java.time.LocalDate;
import java.util.List;

public class HabitDetailViewModel extends AndroidViewModel {

    private final HabitRepository repo;
    private final MutableLiveData<Long> habitIdLive = new MutableLiveData<>();

    private final LiveData<HabitWithLogs> habitWithLogs;
    private final LiveData<Integer> cleanCount;
    private final LiveData<List<DayLog>> recentLogs;

    public HabitDetailViewModel(@NonNull Application application) {
        super(application);
        repo = HabitRepository.getInstance(application);

        habitWithLogs = Transformations.switchMap(habitIdLive, id -> repo.getHabitWithLogs(id));
        cleanCount    = Transformations.switchMap(habitIdLive, id -> repo.getCleanDaysCount(id));
        recentLogs    = Transformations.switchMap(habitIdLive, id -> repo.getRecentLogs(id, 30));
    }

    public void setHabitId(long id) { habitIdLive.setValue(id); }

    public LiveData<HabitWithLogs> getHabitWithLogs() { return habitWithLogs; }
    public LiveData<Integer> getCleanCount() { return cleanCount; }
    public LiveData<List<DayLog>> getRecentLogs() { return recentLogs; }

    public void markClean(LocalDate date, Integer craving, String notes) {
        Long id = habitIdLive.getValue();
        if (id != null) repo.logCleanDay(id, date, craving, notes);
    }

    public void markBreak(LocalDate date, Integer craving, String trigger, String notes) {
        Long id = habitIdLive.getValue();
        if (id != null) repo.logBreak(id, date, craving, trigger, notes);
    }

    public void delete() {
        Long id = habitIdLive.getValue();
        if (id == null) return;
        HabitWithLogs hwl = habitWithLogs.getValue();
        if (hwl != null) repo.deleteHabit(hwl.habit);
    }

    public void update(Habit h) { repo.updateHabit(h); }

    public Long getCurrentHabitId() { return habitIdLive.getValue(); }
}
