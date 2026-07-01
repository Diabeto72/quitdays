package com.example.quitdaystry.activities;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.lifecycle.ViewModelProvider;

import com.example.quitdaystry.R;
import com.example.quitdaystry.models.Habit;
import com.example.quitdaystry.models.Habit.HabitCategory;
import com.example.quitdaystry.utils.ValidationUtils;
import com.example.quitdaystry.viewmodels.HabitViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * One screen for both adding and editing a habit.
 * Add mode: opened without extras. Edit mode: opened with EXTRA_HABIT_ID —
 * the form is pre-filled and saving updates instead of inserting.
 */
public class AddHabitActivity extends BaseActivity {

    public static final String EXTRA_HABIT_ID = "extra_habit_id";

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private TextInputEditText etName, etDailyCost, etMotivation;
    private Spinner spinnerCategory;
    private Button btnPickDate, btnPickTime;
    // Holds the selected quit date (default is today)
    private LocalDate selectedDate = LocalDate.now();
    // Holds the selected quit time (default is now)
    private LocalTime selectedTime = LocalTime.now();
    private String selectedColor = "#4CAF50";

    private HabitViewModel viewModel;
    private long habitId = -1;   // -1 = add mode, otherwise edit mode
    private Habit editingHabit;  // original habit in edit mode, to preserve fields not on the form

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
        setupTimePicker();
        setupColorPicker();

        viewModel = new ViewModelProvider(this).get(HabitViewModel.class);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        habitId = getIntent().getLongExtra(EXTRA_HABIT_ID, -1);
        if (habitId != -1) {
            toolbar.setTitle(R.string.edit);
            loadHabitForEdit();
        }

        FloatingActionButton fabSave = findViewById(R.id.fab_save);
        fabSave.setOnClickListener(v -> onSave());
    }

    private void bindViews() {
        etName = findViewById(R.id.et_name);
        etDailyCost = findViewById(R.id.et_daily_cost);
        etMotivation = findViewById(R.id.et_motivation);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnPickDate = findViewById(R.id.btn_pick_date);
        btnPickTime = findViewById(R.id.btn_pick_time);
    }

    private void setupCategorySpinner() {
        String[] names = getResources().getStringArray(R.array.habit_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        // Display initial or existing date on the button
        btnPickDate.setText(selectedDate.toString());
        btnPickDate.setOnClickListener(v -> {
            // Only allow picking dates in the past (up to today)
            CalendarConstraints constraints = new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now())
                    .build();
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.pick_quit_date)
                    .setCalendarConstraints(constraints)
                    .build();
            picker.addOnPositiveButtonClickListener(ms -> {
                // Convert milliseconds to LocalDate and update UI
                selectedDate = Instant.ofEpochMilli(ms)
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                btnPickDate.setText(selectedDate.toString());
            });
            picker.show(getSupportFragmentManager(), "date_picker");
        });
    }

    private void setupTimePicker() {
        // Display initial or existing time on the button
        btnPickTime.setText(selectedTime.format(TIME_FORMATTER));
        btnPickTime.setOnClickListener(v -> new TimePickerDialog(this, (tp, h, m) -> {
            // Update selected time and button text when time is chosen
            selectedTime = LocalTime.of(h, m);
            btnPickTime.setText(selectedTime.format(TIME_FORMATTER));
        }, selectedTime.getHour(), selectedTime.getMinute(), true).show());
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

    /** Edit mode: fill the form once with the existing habit's values. */
    private void loadHabitForEdit() {
        viewModel.setHabitId(habitId);
        viewModel.getHabitWithLogs().observe(this, hwl -> {
            if (hwl == null) return;
            Habit h = hwl.habit;
            editingHabit = h;
            etName.setText(h.getName());
            etDailyCost.setText(String.valueOf(h.getDailyCost()));
            etMotivation.setText(h.getMotivationNote());
            if (h.getQuitDate() != null) {
                selectedDate = h.getQuitDate();
                btnPickDate.setText(selectedDate.toString());
            }
            if (h.getQuitTime() != null) {
                selectedTime = h.getQuitTime();
                btnPickTime.setText(selectedTime.format(TIME_FORMATTER));
            }
            if (h.getCategory() != null) {
                HabitCategory[] cats = HabitCategory.values();
                for (int i = 0; i < cats.length; i++) {
                    if (cats[i] == h.getCategory()) { spinnerCategory.setSelection(i); break; }
                }
            }
            if (h.getColorHex() != null) selectedColor = h.getColorHex();
            viewModel.getHabitWithLogs().removeObservers(this);
        });
    }

    private void onSave() {
        Habit habit = buildHabitFromForm();
        ValidationUtils.ValidationResult result = ValidationUtils.validateHabit(habit);
        if (!result.ok) { showError(result.message); return; }

        if (habitId == -1) {
            viewModel.insert(habit);
        } else {
            habit.setId(habitId);
            // Preserve fields the form doesn't edit — otherwise update() resets them
            if (editingHabit != null) {
                habit.setBestStreak(editingHabit.getBestStreak());
                habit.setCreatedAt(editingHabit.getCreatedAt());
                habit.setArchived(editingHabit.isArchived());
            }
            viewModel.update(habit);
        }
        finish();
    }

    /** Reads the form fields into a Habit object. */
    private Habit buildHabitFromForm() {
        Habit h = new Habit();
        h.setName(etName.getText() != null ? etName.getText().toString().trim() : "");
        h.setQuitDate(selectedDate);
        h.setQuitTime(selectedTime);
        h.setMotivationNote(etMotivation.getText() != null ? etMotivation.getText().toString().trim() : "");
        h.setColorHex(selectedColor);
        h.setCurrency("₪");

        try {
            String costStr = etDailyCost.getText() != null ? etDailyCost.getText().toString().trim() : "0";
            h.setDailyCost(Double.parseDouble(costStr));
        } catch (NumberFormatException e) {
            h.setDailyCost(0);
        }

        HabitCategory[] cats = HabitCategory.values();
        int pos = spinnerCategory.getSelectedItemPosition();
        h.setCategory((pos >= 0 && pos < cats.length) ? cats[pos] : HabitCategory.OTHER);
        return h;
    }
}
