package com.example.quitdaystry.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.quitdaystry.db.dao.AppDao;
import com.example.quitdaystry.models.DayLog;
import com.example.quitdaystry.models.DayLog.LogStatus;
import com.example.quitdaystry.models.Habit;
import com.example.quitdaystry.models.Habit.HabitCategory;

import java.time.LocalDate;

@Database(entities = {Habit.class, DayLog.class}, version = 2, exportSchema = false)
@TypeConverters(AppDatabase.Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract AppDao appDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE habits ADD COLUMN best_streak INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "quitdays.db"
                    ).addMigrations(MIGRATION_1_2).build();
                }
            }
        }
        return instance;
    }

    /** Converts types Room can't store directly (LocalDate, enums) to/from Strings. */
    public static class Converters {

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
}
