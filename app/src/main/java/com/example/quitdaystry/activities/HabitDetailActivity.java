package com.example.quitdaystry.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.quitdaystry.R;
import com.example.quitdaystry.models.DayLog;
import com.example.quitdaystry.models.Habit;
import com.example.quitdaystry.models.Habit.HabitWithLogs;
import com.example.quitdaystry.utils.DateUtils;
import com.example.quitdaystry.viewmodels.HabitViewModel;
import com.google.android.material.slider.Slider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitDetailActivity extends BaseActivity {

    public static final String EXTRA_HABIT_ID = "extra_habit_id";

    private HabitViewModel viewModel;

    private TextView tvStreakDays, tvCleanTotal, tvLongestStreak, tvMoneySaved, tvToolbarTitle;
    private Button btnBreak, btnClean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_detail);

        long habitId = getIntent().getLongExtra(EXTRA_HABIT_ID, -1);
        if (habitId == -1) { finish(); return; }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvToolbarTitle  = findViewById(R.id.tv_toolbar_title);
        tvStreakDays    = findViewById(R.id.tv_streak_days);
        tvCleanTotal    = findViewById(R.id.tv_clean_total);
        tvLongestStreak = findViewById(R.id.tv_longest_streak);
        tvMoneySaved    = findViewById(R.id.tv_money_saved);
        btnBreak        = findViewById(R.id.btn_break);
        btnClean        = findViewById(R.id.btn_clean);

        viewModel = new ViewModelProvider(this).get(HabitViewModel.class);
        viewModel.setHabitId(habitId);

        viewModel.getHabitWithLogs().observe(this, hwl -> {
            if (hwl == null) return;
            Habit h = hwl.habit;
            List<DayLog> logs = hwl.logs != null ? hwl.logs : new ArrayList<>();

            if (tvToolbarTitle != null) tvToolbarTitle.setText(h.getName());
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(h.getName());

            int current = DateUtils.currentStreak(h, logs);
            tvStreakDays.setText(String.valueOf(current));
            int best = Math.max(current, h.getBestStreak());
            tvLongestStreak.setText(getString(R.string.longest_streak_fmt, best));
        });

        viewModel.getCleanCount().observe(this, count -> {
            if (count == null) count = 0;
            tvCleanTotal.setText(getString(R.string.clean_days_fmt, count));
            HabitWithLogs hwl = viewModel.getHabitWithLogs().getValue();
            if (hwl != null) tvMoneySaved.setText(DateUtils.savedString(count, hwl.habit));
        });

        btnBreak.setOnClickListener(v -> showBreakDialog());
        btnClean.setOnClickListener(v -> showCleanDialog());
    }

    /** Dialog for logging a clean day — saves a CLEAN DayLog to the database. */
    private void showCleanDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_log_clean, null);
        Slider sliderCraving = view.findViewById(R.id.slider_craving);
        EditText etNotes     = view.findViewById(R.id.et_notes);

        new AlertDialog.Builder(this)
                .setTitle(R.string.i_was_clean_today)
                .setView(view)
                .setPositiveButton(R.string.save, (d, w) -> {
                    int craving = (int) sliderCraving.getValue();
                    String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : null;
                    viewModel.markClean(LocalDate.now(), craving, notes);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showBreakDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_log_break, null);
        Slider sliderCraving = view.findViewById(R.id.slider_craving);
        EditText etTrigger   = view.findViewById(R.id.et_trigger);
        EditText etNotes     = view.findViewById(R.id.et_notes);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.i_slipped)
                .setView(view)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(bv -> {
            String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";
            if (notes.isEmpty()) {
                Toast.makeText(this, R.string.error_break_note_required, Toast.LENGTH_SHORT).show();
                return;
            }
            int craving = (int) sliderCraving.getValue();
            String trigger = etTrigger.getText() != null ? etTrigger.getText().toString().trim() : null;
            viewModel.markBreak(LocalDate.now(), craving, trigger, notes);
            dialog.dismiss();
        }));

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.habit_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish(); return true;
        } else if (id == R.id.action_edit) {
            Intent intent = new Intent(this, AddHabitActivity.class);
            intent.putExtra(AddHabitActivity.EXTRA_HABIT_ID, viewModel.getCurrentHabitId());
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete) {
            showDeleteConfirm(); return true;
        } else if (id == R.id.action_archive) {
            archiveHabit(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirm() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_habit)
                .setMessage(R.string.delete_habit_confirm)
                .setPositiveButton(R.string.delete, (d, w) -> { viewModel.delete(); finish(); })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void archiveHabit() {
        HabitWithLogs hwl = viewModel.getHabitWithLogs().getValue();
        if (hwl != null) {
            hwl.habit.setArchived(true);
            viewModel.update(hwl.habit);
            finish();
        }
    }
}
