package com.example.quitdaystry.db.converters;

import androidx.room.TypeConverter;

import com.example.quitdaystry.models.DayLog.LogStatus;
import com.example.quitdaystry.models.Habit.HabitCategory;

import java.time.LocalDate;

public class Converters {

    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        return date == null ? null : date.toString();
    }

    @TypeConverter
    public static LocalDate toLocalDate(String value) {
        return value == null ? null : LocalDate.parse(value);
    }

    @TypeConverter
    public static String fromHabitCategory(HabitCategory category) {
        return category == null ? null : category.name();
    }

    @TypeConverter
    public static HabitCategory toHabitCategory(String value) {
        return value == null ? null : HabitCategory.valueOf(value);
    }

    @TypeConverter
    public static String fromLogStatus(LogStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static LogStatus toLogStatus(String value) {
        return value == null ? null : LogStatus.valueOf(value);
    }
}
