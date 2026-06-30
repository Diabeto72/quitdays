package com.example.quitdaystry.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quitdaystry.R;
import com.example.quitdaystry.activities.AddHabitActivity;
import com.example.quitdaystry.adapters.HabitsAdapter;
import com.example.quitdaystry.viewmodels.HabitViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Fragment showing the list of active habits.
 * Hosts a FAB to add new habits and shows an empty state when the list is empty.
 */
public class testFragment extends Fragment {

    private HabitsAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}