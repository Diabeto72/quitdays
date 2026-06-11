package com.example.quitdaystry.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quitdaystry.R;
import com.example.quitdaystry.activities.HabitDetailActivity;
import com.example.quitdaystry.models.Habit.HabitWithLogs;
import com.example.quitdaystry.utils.DateUtils;

import java.util.Collections;
import java.util.List;

public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.ViewHolder> {

    private List<HabitWithLogs> habits;

    public HabitsAdapter(List<HabitWithLogs> habits) {
        this.habits = habits;
    }

    public void setHabits(List<HabitWithLogs> habits) {
        this.habits = habits;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HabitWithLogs hwl = habits.get(position);
        int streak = DateUtils.currentStreak(hwl.habit, hwl.logs != null ? hwl.logs : Collections.emptyList());

        holder.tvName.setText(hwl.habit.getName());
        holder.tvCategory.setText(hwl.habit.getCategory() != null ? hwl.habit.getCategory().name() : "");
        holder.tvStreak.setText(holder.itemView.getContext().getString(R.string.streak_days_fmt, streak));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), HabitDetailActivity.class);
            intent.putExtra(HabitDetailActivity.EXTRA_HABIT_ID, hwl.habit.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return habits.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvStreak;

        ViewHolder(View v) {
            super(v);
            tvName     = v.findViewById(R.id.tv_habit_name);
            tvCategory = v.findViewById(R.id.tv_habit_category);
            tvStreak   = v.findViewById(R.id.tv_habit_streak);
        }
    }
}
