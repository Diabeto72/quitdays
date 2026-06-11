package com.example.quitdaystry.activities;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.example.quitdaystry.models.Habit;
import com.example.quitdaystry.models.Habit.HabitCategory;
import com.example.quitdaystry.models.HabitDraft;
import com.example.quitdaystry.utils.ValidationUtils;
import com.example.quitdaystry.viewmodels.HabitDetailViewModel;

public class EditHabitActivity extends AddHabitActivity {

    public static final String EXTRA_HABIT_ID = "extra_habit_id";

    private HabitDetailViewModel viewModel;
    private long habitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        habitId = getIntent().getLongExtra(EXTRA_HABIT_ID, -1);
        if (habitId == -1) { finish(); return; }

        viewModel = new ViewModelProvider(this).get(HabitDetailViewModel.class);
        viewModel.setHabitId(habitId);

        viewModel.getHabitWithLogs().observe(this, hwl -> {
            if (hwl == null) return;
            Habit h = hwl.habit;
            if (etName != null) etName.setText(h.getName());
            if (etDailyCost != null) etDailyCost.setText(String.valueOf(h.getDailyCost()));
            if (etMotivation != null) etMotivation.setText(h.getMotivationNote());
            if (h.getQuitDate() != null) {
                selectedDate = h.getQuitDate();
                if (btnPickDate != null) btnPickDate.setText(selectedDate.toString());
            }
            if (h.getCategory() != null && spinnerCategory != null) {
                HabitCategory[] cats = HabitCategory.values();
                for (int i = 0; i < cats.length; i++) {
                    if (cats[i] == h.getCategory()) { spinnerCategory.setSelection(i); break; }
                }
            }
            if (h.getColorHex() != null) selectedColor = h.getColorHex();
            viewModel.getHabitWithLogs().removeObservers(this);
        });
    }

    @Override
    protected void onSave() {
        HabitDraft draft = buildDraft();
        ValidationUtils.ValidationResult result = ValidationUtils.validateHabit(draft);
        if (!result.ok) { showError(result.message); return; }

        Habit updated = draft.toHabit();
        updated.setId(habitId);
        viewModel.update(updated);
        finish();
    }
}
