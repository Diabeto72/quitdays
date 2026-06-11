package com.example.quitdaystry.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.quitdaystry.R;
import com.example.quitdaystry.models.Habit;
import com.example.quitdaystry.models.Habit.HabitCategory;
import com.example.quitdaystry.models.HabitDraft;
import com.example.quitdaystry.utils.ValidationUtils;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class AddHabitActivity extends BaseActivity {

    protected TextInputEditText etName, etDailyCost, etMotivation;
    protected TextInputLayout tilName, tilDailyCost;
    protected Spinner spinnerCategory;
    protected Button btnPickDate;
    protected LocalDate selectedDate = LocalDate.now();
    protected String selectedColor = "#4CAF50";

    private final String[] COLORS = {
        "#4CAF50", "#2196F3", "#F44336", "#FF9800", "#9C27B0", "#00BCD4"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        bindViews();
        setupCategorySpinner();
        setupDatePicker();
        setupColorPicker();

        FloatingActionButton fabSave = findViewById(R.id.fab_save);
        fabSave.setOnClickListener(v -> onSave());
    }

    protected void bindViews() {
        etName = findViewById(R.id.et_name);
        etDailyCost = findViewById(R.id.et_daily_cost);
        etMotivation = findViewById(R.id.et_motivation);
        tilName = findViewById(R.id.til_name);
        tilDailyCost = findViewById(R.id.til_daily_cost);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnPickDate = findViewById(R.id.btn_pick_date);
    }

    private void setupCategorySpinner() {
        String[] names = getResources().getStringArray(R.array.habit_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        btnPickDate.setText(selectedDate.toString());
        btnPickDate.setOnClickListener(v -> {
            CalendarConstraints constraints = new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now())
                    .build();
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.pick_quit_date)
                    .setCalendarConstraints(constraints)
                    .build();
            picker.addOnPositiveButtonClickListener(ms -> {
                selectedDate = Instant.ofEpochMilli(ms)
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                btnPickDate.setText(selectedDate.toString());
            });
            picker.show(getSupportFragmentManager(), "date_picker");
        });
    }

    private void setupColorPicker() {
        int[] colorViewIds = {
            R.id.color_1, R.id.color_2, R.id.color_3,
            R.id.color_4, R.id.color_5, R.id.color_6
        };
        for (int i = 0; i < colorViewIds.length; i++) {
            final String color = COLORS[i];
            android.view.View v = findViewById(colorViewIds[i]);
            if (v != null) {
                v.setBackgroundColor(Color.parseColor(color));
                v.setOnClickListener(x -> selectedColor = color);
            }
        }
    }

    protected void onSave() {
        HabitDraft draft = buildDraft();
        ValidationUtils.ValidationResult result = ValidationUtils.validateHabit(draft);
        if (!result.ok) { showError(result.message); return; }
        Habit habit = draft.toHabit();
        repo().insertHabit(habit, id -> finish());
    }

    protected HabitDraft buildDraft() {
        HabitDraft draft = new HabitDraft();
        draft.name = etName.getText() != null ? etName.getText().toString().trim() : "";
        draft.quitDate = selectedDate;
        draft.dailyCostStr = etDailyCost.getText() != null ? etDailyCost.getText().toString().trim() : "0";
        draft.motivationNote = etMotivation.getText() != null ? etMotivation.getText().toString().trim() : "";
        draft.colorHex = selectedColor;

        HabitCategory[] cats = HabitCategory.values();
        int pos = spinnerCategory.getSelectedItemPosition();
        draft.category = (pos >= 0 && pos < cats.length) ? cats[pos] : HabitCategory.OTHER;
        return draft;
    }
}
