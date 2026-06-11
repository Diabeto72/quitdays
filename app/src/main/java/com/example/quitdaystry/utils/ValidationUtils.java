package com.example.quitdaystry.utils;

import com.example.quitdaystry.models.Habit;

import java.time.LocalDate;

/**
 * Input validation for the add/edit habit form.
 * Each check returns a {@link ValidationResult} with the first failing field's message.
 */
public class ValidationUtils {

    private ValidationUtils() {}

    /** Carries the result of a validation check. */
    public static class ValidationResult {
        public final boolean ok;
        public final String message;

        private ValidationResult(boolean ok, String message) {
            this.ok = ok;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(String msg) {
            return new ValidationResult(false, msg);
        }
    }

    /** Name must be 2–50 non-blank characters. */
    public static ValidationResult validateHabitName(String name) {
        if (name == null || name.trim().isEmpty())
            return ValidationResult.failure("שם ההרגל לא יכול להיות ריק");
        if (name.trim().length() < 2)
            return ValidationResult.failure("שם ההרגל חייב להכיל לפחות 2 תווים");
        if (name.trim().length() > 50)
            return ValidationResult.failure("שם ההרגל לא יכול לעלות על 50 תווים");
        return ValidationResult.success();
    }

    /** Daily cost must be 0–10,000. */
    public static ValidationResult validateDailyCost(double cost) {
        if (cost < 0) return ValidationResult.failure("עלות יומית לא יכולה להיות שלילית");
        if (cost > 10000) return ValidationResult.failure("עלות יומית לא יכולה לעלות על 10,000");
        return ValidationResult.success();
    }

    /** Quit date must not be in the future and not before 1990. */
    public static ValidationResult validateQuitDate(LocalDate date) {
        if (date == null) return ValidationResult.failure("תאריך גמילה חובה");
        if (date.isAfter(LocalDate.now()))
            return ValidationResult.failure("תאריך גמילה לא יכול להיות בעתיד");
        if (date.getYear() < 1990)
            return ValidationResult.failure("תאריך גמילה לא יכול להיות לפני 1990");
        return ValidationResult.success();
    }

    /** Motivation note must be ≤ 500 chars (nullable). */
    public static ValidationResult validateMotivation(String note) {
        if (note != null && note.length() > 500)
            return ValidationResult.failure("הערת מוטיבציה לא יכולה לעלות על 500 תווים");
        return ValidationResult.success();
    }

    /** Validates a full habit; returns the first failing result or success. */
    public static ValidationResult validateHabit(Habit h) {
        ValidationResult r;
        r = validateHabitName(h.getName());
        if (!r.ok) return r;
        r = validateQuitDate(h.getQuitDate());
        if (!r.ok) return r;
        r = validateDailyCost(h.getDailyCost());
        if (!r.ok) return r;
        r = validateMotivation(h.getMotivationNote());
        if (!r.ok) return r;
        return ValidationResult.success();
    }
}
