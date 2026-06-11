package com.example.quitdaystry.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quitdaystry.R;
import com.example.quitdaystry.models.DayLog;
import com.example.quitdaystry.models.DayLog.LogStatus;

import java.util.List;

public class DayLogAdapter extends RecyclerView.Adapter<DayLogAdapter.ViewHolder> {

    private List<DayLog> logs;

    public DayLogAdapter(List<DayLog> logs) {
        this.logs = logs;
    }

    public void setLogs(List<DayLog> logs) {
        this.logs = logs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_log, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DayLog log = logs.get(position);
        holder.tvDate.setText(log.getLogDate() != null ? log.getLogDate().toString() : "");
        boolean isClean = log.getStatus() == LogStatus.CLEAN;
        holder.tvStatus.setText(isClean ? "✓" : "✗");
        holder.tvStatus.setTextColor(isClean ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));

        String craving = log.getCravingLevel() != null ? String.valueOf(log.getCravingLevel()) : "-";
        holder.tvCraving.setText(holder.itemView.getContext().getString(R.string.craving_fmt, craving));
        holder.tvNotes.setText(log.getNotes() != null ? log.getNotes() : "");
    }

    @Override
    public int getItemCount() { return logs.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvStatus, tvCraving, tvNotes;

        ViewHolder(View v) {
            super(v);
            tvDate    = v.findViewById(R.id.tv_log_date);
            tvStatus  = v.findViewById(R.id.tv_log_status);
            tvCraving = v.findViewById(R.id.tv_log_craving);
            tvNotes   = v.findViewById(R.id.tv_log_notes);
        }
    }
}
