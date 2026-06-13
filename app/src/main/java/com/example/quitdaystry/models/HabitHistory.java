package com.example.quitdaystry.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

/** Frozen snapshot of a habit attempt that ended (the user logged a slip-up). */
@Entity(tableName = "habit_history")
public class HabitHistory {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "category")
    private Habit.HabitCategory category;

    @ColumnInfo(name = "color_hex")
    private String colorHex;

    @ColumnInfo(name = "currency")
    private String currency;

    @ColumnInfo(name = "daily_cost")
    private double dailyCost;

    @ColumnInfo(name = "start_date")
    private LocalDate startDate;

    @ColumnInfo(name = "end_date")
    private LocalDate endDate;

    @ColumnInfo(name = "clean_days")
    private int cleanDays;

    @ColumnInfo(name = "best_streak")
    private int bestStreak;

    @ColumnInfo(name = "failure_note")
    private String failureNote;

    @ColumnInfo(name = "ended_at")
    private long endedAt;

    public HabitHistory() {
        this.endedAt = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Habit.HabitCategory getCategory() { return category; }
    public void setCategory(Habit.HabitCategory category) { this.category = category; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public double getDailyCost() { return dailyCost; }
    public void setDailyCost(double dailyCost) { this.dailyCost = dailyCost; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getCleanDays() { return cleanDays; }
    public void setCleanDays(int cleanDays) { this.cleanDays = cleanDays; }

    public int getBestStreak() { return bestStreak; }
    public void setBestStreak(int bestStreak) { this.bestStreak = bestStreak; }

    public String getFailureNote() { return failureNote; }
    public void setFailureNote(String failureNote) { this.failureNote = failureNote; }

    public long getEndedAt() { return endedAt; }
    public void setEndedAt(long endedAt) { this.endedAt = endedAt; }
}
