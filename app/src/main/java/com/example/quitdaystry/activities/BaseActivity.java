package com.example.quitdaystry.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quitdaystry.repositories.HabitRepository;

/**
 * Base activity that enforces RTL layout direction and provides shared helpers.
 * All activities in QuitDays extend this class.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    /** Shows a short toast with the given error message. */
    protected void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /** @return the singleton HabitRepository. */
    protected HabitRepository repo() {
        return HabitRepository.getInstance(getApplication());
    }
}
