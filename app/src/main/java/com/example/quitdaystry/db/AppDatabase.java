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
import com.example.quitdaystry.models.HabitHistory;

import java.time.LocalDate;
import java.time.LocalTime;

@Database(entities = {Habit.class, DayLog.class, HabitHistory.class}, version = 4, exportSchema = false)
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

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE habits ADD COLUMN quit_time TEXT");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS habit_history (" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "category TEXT, " +
                    "color_hex TEXT, " +
                    "currency TEXT, " +
                    "daily_cost REAL NOT NULL, " +
                    "start_date TEXT, " +
                    "end_date TEXT, " +
                    "clean_days INTEGER NOT NULL, " +
                    "best_streak INTEGER NOT NULL, " +
                    "failure_note TEXT, " +
                    "ended_at INTEGER NOT NULL)");
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
                    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build();
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
        public static String fromLocalTime(LocalTime time) {
            return time == null ? null : time.toString();
        }

        @TypeConverter
        public static LocalTime toLocalTime(String value) {
            return value == null ? null : LocalTime.parse(value);
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
