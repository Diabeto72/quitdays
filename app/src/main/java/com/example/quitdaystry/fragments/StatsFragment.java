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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quitdaystry.R;
import com.example.quitdaystry.adapters.HabitStatsAdapter;
import com.example.quitdaystry.models.StatsEntry;
import com.example.quitdaystry.viewmodels.HabitViewModel;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private TextView tvHabits, tvCleanDays, tvMoney, tvBestStreak;
    private HabitStatsAdapter adapter;

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

        RecyclerView rv = view.findViewById(R.id.rv_stats_entries);
        adapter = new HabitStatsAdapter(Collections.emptyList());
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        HabitViewModel vm = new ViewModelProvider(this).get(HabitViewModel.class);
        vm.getStatsEntries().observe(getViewLifecycleOwner(), this::updateStats);
    }

    private void updateStats(List<StatsEntry> entries) {
        if (entries == null) entries = Collections.emptyList();

        adapter.setEntries(entries);

        int activeCount = 0;
        int totalClean = 0;
        double totalMoney = 0;
        int bestStreak = 0;

        for (StatsEntry entry : entries) {
            if (entry.active) activeCount++;
            totalClean += entry.cleanDays;
            totalMoney += entry.moneySaved();
            if (entry.streak > bestStreak) bestStreak = entry.streak;
        }

        tvHabits.setText(String.valueOf(activeCount));
        tvCleanDays.setText(String.valueOf(totalClean));
        tvMoney.setText(String.format(Locale.US, "₪ %.2f", totalMoney));
        tvBestStreak.setText(getString(R.string.streak_days_fmt, bestStreak));
    }
}
