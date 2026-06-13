package com.example.quitdaystry.models;

import java.time.LocalDate;

/** Unified view of a habit (active or ended) for the stats list. */
public class StatsEntry {

    public final String name;
    public final Habit.HabitCategory category;
    public final String colorHex;
    public final String currency;
    public final double dailyCost;
    public final int cleanDays;
    public final int streak;
    public final LocalDate startDate;
    public final LocalDate endDate; // null when the habit is still active
    public final boolean active;

    public StatsEntry(String name, Habit.HabitCategory category, String colorHex, String currency,
                       double dailyCost, int cleanDays, int streak,
                       LocalDate startDate, LocalDate endDate, boolean active) {
        this.name = name;
        this.category = category;
        this.colorHex = colorHex;
        this.currency = currency;
        this.dailyCost = dailyCost;
        this.cleanDays = cleanDays;
        this.streak = streak;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    public double moneySaved() {
        return cleanDays * dailyCost;
    }
}
