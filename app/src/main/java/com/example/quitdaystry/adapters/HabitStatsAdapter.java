package com.example.quitdaystry.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quitdaystry.R;
import com.example.quitdaystry.models.StatsEntry;
import com.example.quitdaystry.utils.DateUtils;

import java.util.List;
import java.util.Locale;

public class HabitStatsAdapter extends RecyclerView.Adapter<HabitStatsAdapter.ViewHolder> {

    private List<StatsEntry> entries;

    public HabitStatsAdapter(List<StatsEntry> entries) {
        this.entries = entries;
    }

    public void setEntries(List<StatsEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stats_entry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatsEntry entry = entries.get(position);
        android.content.Context ctx = holder.itemView.getContext();

        holder.tvName.setText(entry.name);

        String[] categories = ctx.getResources().getStringArray(R.array.habit_categories);
        if (entry.category != null && entry.category.ordinal() < categories.length) {
            holder.tvCategory.setText(categories[entry.category.ordinal()]);
        } else {
            holder.tvCategory.setText("");
        }

        if (entry.active) {
            holder.tvStatus.setText(R.string.status_active);
            holder.tvStatus.setTextColor(ctx.getColor(R.color.clean_green));
            holder.tvStreak.setText(ctx.getString(R.string.streak_days_fmt, entry.streak));
        } else {
            holder.tvStatus.setText(R.string.status_ended);
            holder.tvStatus.setTextColor(ctx.getColor(R.color.break_red));
            holder.tvStreak.setText(ctx.getString(R.string.longest_streak_fmt, entry.streak));
        }

        holder.tvCleanDays.setText(ctx.getString(R.string.clean_days_fmt, entry.cleanDays));
        holder.tvMoney.setText(String.format(Locale.US, "%s %.2f", entry.currency, entry.moneySaved()));

        if (entry.endDate != null) {
            holder.tvDates.setText(ctx.getString(R.string.date_range_fmt,
                    DateUtils.format(entry.startDate), DateUtils.format(entry.endDate)));
        } else {
            holder.tvDates.setText(ctx.getString(R.string.date_range_ongoing_fmt,
                    DateUtils.format(entry.startDate)));
        }

        try {
            holder.vColorStrip.setBackgroundColor(Color.parseColor(entry.colorHex));
        } catch (Exception e) {
            holder.vColorStrip.setBackgroundColor(ctx.getColor(R.color.primary));
        }
    }

    @Override
    public int getItemCount() { return entries.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvStatus, tvStreak, tvCleanDays, tvMoney, tvDates;
        View vColorStrip;

        ViewHolder(View v) {
            super(v);
            tvName       = v.findViewById(R.id.tv_entry_name);
            tvCategory   = v.findViewById(R.id.tv_entry_category);
            tvStatus     = v.findViewById(R.id.tv_entry_status);
            tvStreak     = v.findViewById(R.id.tv_entry_streak);
            tvCleanDays  = v.findViewById(R.id.tv_entry_clean_days);
            tvMoney      = v.findViewById(R.id.tv_entry_money);
            tvDates      = v.findViewById(R.id.tv_entry_dates);
            vColorStrip  = v.findViewById(R.id.v_color_strip);
        }
    }
}
