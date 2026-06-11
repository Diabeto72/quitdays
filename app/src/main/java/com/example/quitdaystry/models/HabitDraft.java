package com.example.quitdaystry.models;

import com.example.quitdaystry.models.Habit.HabitCategory;

import java.time.LocalDate;

/** Plain POJO used by Add/Edit screens to hold form state before persisting. Not a Room entity. */
public class HabitDraft {

    public String name;
    public HabitCategory category;
    public LocalDate quitDate;
    public String dailyCostStr;
    public String currency = "₪";
    public String colorHex;
    public String motivationNote;

    public Habit toHabit() {
        Habit h = new Habit();
        h.setName(name);
        h.setCategory(category);
        h.setQuitDate(quitDate);
        h.setCurrency(currency);
        h.setColorHex(colorHex);
        h.setMotivationNote(motivationNote);
        try {
            h.setDailyCost(Double.parseDouble(dailyCostStr));
        } catch (Exception e) {
            h.setDailyCost(0);
        }
        return h;
    }
}
