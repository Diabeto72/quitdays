package com.example.quitdaystry.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.quitdaystry.models.Habit.HabitWithLogs;
import com.example.quitdaystry.repositories.HabitRepository;

import java.util.List;

public class HabitsViewModel extends AndroidViewModel {

    private final HabitRepository repo;

    public HabitsViewModel(@NonNull Application application) {
        super(application);
        repo = HabitRepository.getInstance(application);
    }

    public LiveData<List<HabitWithLogs>> getActiveHabits() {
        return repo.getActiveHabits();
    }
}
