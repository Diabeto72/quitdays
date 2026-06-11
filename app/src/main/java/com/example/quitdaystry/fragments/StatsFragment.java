package com.example.quitdaystry.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.quitdaystry.R;
import com.example.quitdaystry.models.DayLog;
import com.example.quitdaystry.models.DayLog.LogStatus;
import com.example.quitdaystry.models.Habit.HabitWithLogs;
import com.example.quitdaystry.utils.DateUtils;
import com.example.quitdaystry.viewmodels.HabitsViewModel;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private TextView tvHabits, tvCleanDays, tvMoney, tvBestStreak;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvHabits     = view.findViewById(R.id.tv_stat_habits);
        tvCleanDays  = view.findViewById(R.id.tv_stat_clean_days);
        tvMoney      = view.findViewById(R.id.tv_stat_money);
        tvBestStreak = view.findViewById(R.id.tv_stat_best_streak);

        HabitsViewModel vm = new ViewModelProvider(this).get(HabitsViewModel.class);
        vm.getActiveHabits().observe(getViewLifecycleOwner(), this::updateStats);
    }

    private void updateStats(List<HabitWithLogs> habits) {
        if (habits == null) habits = Collections.emptyList();

        int habitCount = habits.size();
        int totalClean = 0;
        double totalMoney = 0;
        int bestStreak = 0;

        for (HabitWithLogs hwl : habits) {
            List<DayLog> logs = hwl.logs != null ? hwl.logs : Collections.emptyList();

            long cleanCount = logs.stream().filter(l -> l.getStatus() == LogStatus.CLEAN).count();
            totalClean += (int) cleanCount;
            totalMoney += cleanCount * hwl.habit.getDailyCost();

            int streak = Math.max(
                    DateUtils.currentStreak(hwl.habit, logs),
                    hwl.habit.getBestStreak());
            if (streak > bestStreak) bestStreak = streak;
        }

        tvHabits.setText(String.valueOf(habitCount));
        tvCleanDays.setText(String.valueOf(totalClean));
        tvMoney.setText(String.format(Locale.US, "₪ %.2f", totalMoney));
        tvBestStreak.setText(getString(R.string.streak_days_fmt, bestStreak));
    }
}
