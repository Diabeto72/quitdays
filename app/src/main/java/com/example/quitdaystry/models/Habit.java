package com.example.quitdaystry.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.time.LocalDate;
import java.util.List;

@Entity(tableName = "habits")
public class Habit {

    public enum HabitCategory {
        SMOKING, ALCOHOL, SUGAR, SOCIAL_MEDIA, GAMBLING, OTHER
    }

    public static class HabitWithLogs {
        @Embedded
        public Habit habit;

        @Relation(parentColumn = "id", entityColumn = "habit_id")
        public List<DayLog> logs;
    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "category")
    private HabitCategory category;

    @ColumnInfo(name = "quit_date")
    private LocalDate quitDate;

    @ColumnInfo(name = "daily_cost")
    private double dailyCost;

    @ColumnInfo(name = "currency")
    private String currency;

    @ColumnInfo(name = "color_hex")
    private String colorHex;

    @ColumnInfo(name = "motivation_note")
    private String motivationNote;

    @ColumnInfo(name = "is_archived")
    private boolean isArchived;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "best_streak")
    private int bestStreak;

    public Habit() {
        this.currency = "₪";
        this.dailyCost = 0;
        this.isArchived = false;
        this.createdAt = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public HabitCategory getCategory() { return category; }
    public void setCategory(HabitCategory category) { this.category = category; }

    public LocalDate getQuitDate() { return quitDate; }
    public void setQuitDate(LocalDate quitDate) { this.quitDate = quitDate; }

    public double getDailyCost() { return dailyCost; }
    public void setDailyCost(double dailyCost) { this.dailyCost = dailyCost; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public String getMotivationNote() { return motivationNote; }
    public void setMotivationNote(String motivationNote) { this.motivationNote = motivationNote; }

    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public int getBestStreak() { return bestStreak; }
    public void setBestStreak(int bestStreak) { this.bestStreak = bestStreak; }
}
