package com.example.quitdaystry.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(
    tableName = "day_logs",
    foreignKeys = @ForeignKey(
        entity = Habit.class,
        parentColumns = "id",
        childColumns = "habit_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {
        @Index(value = "habit_id"),
        @Index(value = {"habit_id", "log_date"}, unique = true)
    }
)
public class DayLog {

    public enum LogStatus { CLEAN, BREAK }

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "habit_id")
    private long habitId;

    @ColumnInfo(name = "log_date")
    private LocalDate logDate;

    @ColumnInfo(name = "status")
    private LogStatus status;

    @ColumnInfo(name = "craving_level")
    private Integer cravingLevel;

    @ColumnInfo(name = "trigger_note")
    private String triggerNote;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "logged_at")
    private long loggedAt;

    public DayLog() {
        this.loggedAt = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getHabitId() { return habitId; }
    public void setHabitId(long habitId) { this.habitId = habitId; }

    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }

    public LogStatus getStatus() { return status; }
    public void setStatus(LogStatus status) { this.status = status; }

    public Integer getCravingLevel() { return cravingLevel; }
    public void setCravingLevel(Integer cravingLevel) { this.cravingLevel = cravingLevel; }

    public String getTriggerNote() { return triggerNote; }
    public void setTriggerNote(String triggerNote) { this.triggerNote = triggerNote; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getLoggedAt() { return loggedAt; }
    public void setLoggedAt(long loggedAt) { this.loggedAt = loggedAt; }
}
