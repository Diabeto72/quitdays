package com.example.quitdaystry.utils;

import com.example.quitdaystry.models.DayLog;
import com.example.quitdaystry.models.DayLog.LogStatus;
import com.example.quitdaystry.models.Habit;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Date formatting, streak calculation, and money-saving helpers. */
public class DateUtils {

    private DateUtils() {}

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(new Locale("he"));

    public static String format(LocalDate date) {
        if (date == null) return "";
        return date.format(DISPLAY_FORMATTER);
    }

    public static String toIso(LocalDate date) {
        return date == null ? "" : date.toString();
    }

    /** Current streak in days. Only an explicit BREAK log breaks the streak. */
    public static int currentStreak(Habit h, List<DayLog> logs) {
        LocalDate today = LocalDate.now();
        LocalDate quitDate = h.getQuitDate();

        LocalDate lastBreak = logs.stream()
                .filter(l -> l.getStatus() == LogStatus.BREAK)
                .map(DayLog::getLogDate)
                .max(Comparator.naturalOrder())
                .orElse(null);

        LocalDate effectiveStart = (lastBreak != null && lastBreak.isAfter(quitDate))
                ? lastBreak.plusDays(1)
                : quitDate;

        if (effectiveStart.isAfter(today)) return 0;
        return (int) ChronoUnit.DAYS.between(effectiveStart, today);
    }

    /** Longest consecutive CLEAN-day span across all logs. */
    public static int longestStreak(List<DayLog> logs) {
        if (logs == null || logs.isEmpty()) return 0;

        List<DayLog> sorted = logs.stream()
                .filter(l -> l.getStatus() == LogStatus.CLEAN)
                .sorted(Comparator.comparing(DayLog::getLogDate))
                .collect(Collectors.toList());

        if (sorted.isEmpty()) return 0;

        int longest = 1, current = 1;
        for (int i = 1; i < sorted.size(); i++) {
            LocalDate prev = sorted.get(i - 1).getLogDate();
            LocalDate curr = sorted.get(i).getLogDate();
            if (ChronoUnit.DAYS.between(prev, curr) == 1) {
                if (++current > longest) longest = current;
            } else {
                current = 1;
            }
        }
        return longest;
    }

    /** Formatted money saved string, e.g. "₪ 150.00". */
    public static String savedString(int cleanDays, Habit habit) {
        double saved = cleanDays * habit.getDailyCost();
        return String.format(Locale.US, "%s %.2f", habit.getCurrency(), saved);
    }

    public static double savedAmount(int cleanDays, Habit habit) {
        return cleanDays * habit.getDailyCost();
    }
}
