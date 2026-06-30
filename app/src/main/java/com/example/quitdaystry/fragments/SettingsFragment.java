package com.example.quitdaystry.fragments;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.quitdaystry.R;
import com.example.quitdaystry.activities.MainActivity;
import com.example.quitdaystry.db.AppDatabase;

import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {

    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = requireContext().getSharedPreferences(MainActivity.PREFS_NAME, android.content.Context.MODE_PRIVATE);

        SwitchCompat switchReminder = view.findViewById(R.id.switch_reminder);
        Button btnExport = view.findViewById(R.id.btn_export);
        Button btnDeleteAll = view.findViewById(R.id.btn_delete_all);

        switchReminder.setChecked(prefs.getBoolean("reminder_enabled", false));

        switchReminder.setOnCheckedChangeListener((btn, checked) -> {
            prefs.edit().putBoolean("reminder_enabled", checked).apply();
            if (checked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requireActivity().requestPermissions(
                            new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
                }
                showTimePicker();
            } else {
                MainActivity.rescheduleReminderIfEnabled(requireContext());
                Toast.makeText(requireContext(), "תזכורת כובתה", Toast.LENGTH_SHORT).show();
            }
        });

        btnExport.setOnClickListener(v ->
                Toast.makeText(requireContext(), "ייצוא יהיה זמין בגרסה הבאה", Toast.LENGTH_SHORT).show());

        btnDeleteAll.setOnClickListener(v -> confirmDeleteAll());
    }

    private void showTimePicker() {
        int hour = prefs.getInt("reminder_hour", 21);
        new TimePickerDialog(requireContext(), (tp, h, m) -> {
            prefs.edit().putInt("reminder_hour", h).apply();
            MainActivity.rescheduleReminderIfEnabled(requireContext());
            Toast.makeText(requireContext(),
                    String.format("תזכורת נקבעה ל-%02d:00", h), Toast.LENGTH_SHORT).show();
        }, hour, 0, true).show();
    }

    private void confirmDeleteAll() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_all_data)
                .setMessage(R.string.delete_all_confirm)
                .setPositiveButton(R.string.delete, (d, w) -> deleteAllData())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteAllData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireActivity().getApplication());
            db.clearAllTables();
        });
    }
}
