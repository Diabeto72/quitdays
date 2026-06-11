package com.example.quitdaystry.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.quitdaystry.models.DayLog;
import com.example.quitdaystry.models.Habit;
import com.example.quitdaystry.models.Habit.HabitWithLogs;

import java.util.List;

@Dao
public interface AppDao {

    // --- Habit ---

    @Insert
    long insertHabit(Habit h);

    @Update
    void updateHabit(Habit h);

    @Delete
    void deleteHabit(Habit h);

    @Query("SELECT * FROM habits WHERE id = :id")
    LiveData<Habit> getHabitById(long id);

    @Query("SELECT * FROM habits WHERE id = :id")
    Habit getHabitByIdSync(long id);

    @Query("SELECT id FROM habits WHERE is_archived = 0")
    List<Long> getActiveHabitIdsSync();

    @Transaction
    @Query("SELECT * FROM habits WHERE is_archived = 0 ORDER BY created_at DESC")
    LiveData<List<HabitWithLogs>> getActiveHabitsWithLogs();

    @Transaction
    @Query("SELECT * FROM habits WHERE id = :id")
    LiveData<HabitWithLogs> getHabitWithLogs(long id);

    // --- DayLog ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLog(DayLog log);

    /** Inserts only if no log exists for that habit+date — keeps BREAK logs intact. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertLogIfAbsent(DayLog log);

    @Delete
    void deleteLog(DayLog log);

    @Query("SELECT COUNT(*) FROM day_logs WHERE habit_id = :habitId AND status = 'CLEAN'")
    LiveData<Integer> getCleanCount(long habitId);

    @Query("SELECT * FROM day_logs WHERE habit_id = :habitId")
    List<DayLog> getLogsForHabitSync(long habitId);
}
